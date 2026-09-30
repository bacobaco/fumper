package com.python4d.fumper;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.File;

public class PhysicsSimTest {
    public static void main(String[] args) {
        new SharedLibraryLoader().load("gdx-box2d");
        World.setVelocityThreshold(0.0f);

        int width = 320;
        int height = 480;
        float WORLD_TO_BOX = (height / 20000.0f) * (width / (float) width);
        float BOX_TO_WORLD = 1.0f / WORLD_TO_BOX;

        File bdFile = new File("texture/body/fumperbody.bd");
        if (!bdFile.exists()) {
            bdFile = new File("fumper-android/assets/texture/body/fumperbody.bd");
        }
        if (!bdFile.exists()) {
            bdFile = new File("../fumper-android/assets/texture/body/fumperbody.bd");
        }
        BodyEditorLoader loader = new BodyEditorLoader(new FileHandle(bdFile));

        float Posx = width / 4.0f; // 80
        float panierX = width / 1.5f; // 213.33
        float panierY = width / 3.0f; // 106.67
        float panierScale = 0.2f * width / 400.0f;
        float imgPanierW = 587.0f * panierScale; // 93.92
        float imgPanierH = 640.0f * panierScale; // 102.4
        float dropY = panierY * 1.5f; // 160

        System.out.println("Basket range: X=[" + panierX + ".." + (panierX + imgPanierW) + "], Y=[" + panierY + ".." + (panierY + imgPanierH) + "]");

        String[] allFruits = {
            "citron", "pomme-dessin", "poire", "pomme_verte", "cerises",
            "citrouille", "pomme-dessin2", "fraise", "orange", "pomme-photo"
        };

        float dropX = Posx - width / 16.0f; // 80 - 20 = 60
        float pushMult = 1.95f;

        System.out.println("Testing all 10 fruits with dropX=" + dropX + ", pushMult=" + pushMult + ":");
        int successCount = 0;
        for (String fName : allFruits) {
            SimResult r = simulate(loader, width, height, WORLD_TO_BOX, BOX_TO_WORLD,
                    fName, dropX, dropY, panierX, panierY, imgPanierW, imgPanierH, pushMult);
            System.out.println(String.format("Fruit %-14s: entered=%-5b settled=%-5b finalPos=(%.1f, %.1f)",
                    fName, r.enteredBasket, r.settledInBasket, r.finalX, r.finalY));
            if (r.enteredBasket) successCount++;
        }
        System.out.println("Total entered basket: " + successCount + " / " + allFruits.length);
    }

    static class SimResult {
        boolean enteredBasket = false;
        boolean settledInBasket = false;
        float finalX = 0, finalY = 0;
    }

    private static SimResult simulate(BodyEditorLoader loader, int width, int height,
                                      float WORLD_TO_BOX, float BOX_TO_WORLD,
                                      String fruitName, float dropX, float dropY,
                                      float panierX, float panierY, float panierW, float panierH,
                                      float pushMultiplier) {
        World world = new World(new Vector2(0, -9.8f), true);

        float Posx = width / 4.0f;
        float Posy = width / 10.0f;
        float size = 0.08f * width / 400.0f;

        float imgBucheW = 341.0f;
        float imgBucheH = 327.0f;
        float imgBucheScale = size;

        float PosxW = Posx * WORLD_TO_BOX;
        float PosyW = Posy * WORLD_TO_BOX;

        BodyDef bdBuche = new BodyDef();
        bdBuche.position.set(PosxW, PosyW);
        bdBuche.type = BodyType.StaticBody;
        Body bodyBuche = world.createBody(bdBuche);

        FixtureDef fdBuche = new FixtureDef();
        fdBuche.friction = 0.50f;
        fdBuche.restitution = 0.3f;
        fdBuche.density = 1.0f;
        loader.attachFixture(bodyBuche, "buche", fdBuche, imgBucheW * imgBucheScale * WORLD_TO_BOX);

        float imgPlancheW = 640.0f;
        float imgPlancheH = 320.0f;
        float imgPlancheScale = size * 3.0f;

        float instable = 1.2f;
        float anchorAx = PosxW + imgBucheW * imgBucheScale * WORLD_TO_BOX / 2f;
        float anchorAy = PosyW + imgBucheH * imgBucheScale * WORLD_TO_BOX;
        float anchorBx = imgPlancheW * imgPlancheScale * WORLD_TO_BOX / 2f * instable;
        float anchorBy = imgPlancheH * imgPlancheScale * WORLD_TO_BOX / 2f;

        BodyDef bdPlanche = new BodyDef();
        bdPlanche.position.set(anchorAx - anchorBx, anchorAy - anchorBy);
        bdPlanche.type = BodyType.DynamicBody;
        Body bodyPlanche = world.createBody(bdPlanche);

        FixtureDef fdPlanche = new FixtureDef();
        fdPlanche.friction = 0.2f;
        fdPlanche.restitution = 0.2f;
        fdPlanche.density = 5.0f;
        loader.attachFixture(bodyPlanche, "planche", fdPlanche, imgPlancheW * imgPlancheScale * WORLD_TO_BOX);

        RevoluteJointDef jd = new RevoluteJointDef();
        jd.enableLimit = true;
        jd.lowerAngle = -30 * MathUtils.degreesToRadians;
        jd.upperAngle = 10 * MathUtils.degreesToRadians;
        jd.bodyA = bodyBuche;
        jd.bodyB = bodyPlanche;
        jd.collideConnected = false;
        jd.localAnchorA.set(anchorAx - PosxW, anchorAy - PosyW);
        jd.localAnchorB.set(anchorBx, anchorBy);
        world.createJoint(jd);

        // Panier
        BodyDef bdPanier = new BodyDef();
        bdPanier.position.set(panierX * WORLD_TO_BOX, panierY * WORLD_TO_BOX);
        bdPanier.type = BodyType.StaticBody;
        Body bodyPanier = world.createBody(bdPanier);

        FixtureDef fdPanier = new FixtureDef();
        fdPanier.friction = 0.80f;
        fdPanier.restitution = 0.10f;
        fdPanier.density = 1.0f;
        loader.attachFixture(bodyPanier, "panier", fdPanier, panierW * WORLD_TO_BOX);

        // Settle bascule
        for (int i = 0; i < 60; i++) {
            world.step(1.0f / 60.0f, 8, 3);
        }

        // Drop fruit
        BodyDef bdFruit = new BodyDef();
        bdFruit.type = BodyType.DynamicBody;
        bdFruit.position.set(dropX * WORLD_TO_BOX, dropY * WORLD_TO_BOX);
        Body bodyFruit = world.createBody(bdFruit);

        FixtureDef fdFruit = new FixtureDef();
        fdFruit.density = 1f;
        fdFruit.friction = 0.5f;
        fdFruit.restitution = 0.2f;
        loader.attachFixture(bodyFruit, fruitName, fdFruit, 32.0f * WORLD_TO_BOX);

        SimResult res = new SimResult();

        // Fall onto plank
        for (int step = 0; step < 90; step++) {
            world.step(1.0f / 60.0f, 8, 3);
            if (bodyFruit.getPosition().y * BOX_TO_WORLD < 0) {
                world.dispose();
                return res;
            }
        }

        // Catapult push
        float forceY = (float) -Math.pow(bodyPlanche.getMass(), 1.90) * pushMultiplier;
        bodyPlanche.setAwake(true);
        bodyPlanche.applyAngularImpulse(forceY, true);

        float pBoxX = panierX * WORLD_TO_BOX;
        float pBoxY = panierY * WORLD_TO_BOX;
        float pBoxW = panierW * WORLD_TO_BOX;
        float pBoxH = panierH * WORLD_TO_BOX;

        for (int step = 0; step < 180; step++) {
            world.step(1.0f / 60.0f, 8, 3);
            float cx = bodyFruit.getWorldCenter().x;
            float cy = bodyFruit.getWorldCenter().y;
            boolean in = (cx > pBoxX && cy > pBoxY && cx < pBoxX + pBoxW && cy < pBoxY + pBoxH);
            if (in) {
                res.enteredBasket = true;
                if (bodyFruit.getLinearVelocity().len() < 0.8f && Math.abs(bodyFruit.getAngularVelocity()) < 1.0f) {
                    res.settledInBasket = true;
                }
            }
        }

        res.finalX = bodyFruit.getPosition().x * BOX_TO_WORLD;
        res.finalY = bodyFruit.getPosition().y * BOX_TO_WORLD;
        world.dispose();
        return res;
    }
}
