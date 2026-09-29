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

        World world = new World(new Vector2(0, -9.8f), true);

        int width = 320;
        int height = 480;
        float WORLD_TO_BOX = (height / 20000.0f) * (width / (float) width);
        float BOX_TO_WORLD = 1.0f / WORLD_TO_BOX;

        float Posx = width / 4.0f;
        float Posy = width / 10.0f;
        float size = 0.08f * width / 400.0f;

        File bdFile = new File("fumper-android/assets/texture/body/fumperbody.bd");
        if (!bdFile.exists()) {
            bdFile = new File("../fumper-android/assets/texture/body/fumperbody.bd");
        }
        BodyEditorLoader loader = new BodyEditorLoader(new FileHandle(bdFile));

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

        System.out.println("Planche mass = " + bodyPlanche.getMass() + ", inertia = " + bodyPlanche.getInertia());
        System.out.println("Initial planche angle = " + (bodyPlanche.getAngle() * MathUtils.radiansToDegrees) + " deg, pos = " + bodyPlanche.getPosition());

        // Step world 60 times (1 second) to see resting position
        for (int i = 0; i < 60; i++) {
            world.step(1.0f / 60.0f, 8, 3);
        }
        System.out.println("Planche resting angle after 60 steps = " + (bodyPlanche.getAngle() * MathUtils.radiansToDegrees) + " deg, pos = " + bodyPlanche.getPosition());

        // Test pushing planche
        float forceY = (float) -Math.pow(bodyPlanche.getMass(), 1.90);
        System.out.println("Applying angular impulse forceY = " + forceY);
        bodyPlanche.applyAngularImpulse(forceY, true);

        for (int i = 0; i < 30; i++) {
            world.step(1.0f / 60.0f, 8, 3);
            if (i % 5 == 0) {
                System.out.println("Step " + i + ": angle = " + (bodyPlanche.getAngle() * MathUtils.radiansToDegrees) + " deg, angVel = " + bodyPlanche.getAngularVelocity());
            }
        }

        // Test all fruit bodies and check for any fallback box or errors
        String[] allBodies = {
            "citron", "pomme-dessin", "poire", "pomme_verte", "cerises",
            "citrouille", "pomme-dessin2", "fraise", "orange", "pomme-photo",
            "buche", "planche", "panier"
        };

        for (String bName : allBodies) {
            BodyDef bdef = new BodyDef();
            bdef.type = BodyType.DynamicBody;
            Body b = world.createBody(bdef);
            FixtureDef fdef = new FixtureDef();
            fdef.density = 1f;
            loader.attachFixture(b, bName, fdef, 32.0f * WORLD_TO_BOX);
            System.out.println("SUCCESS: Body '" + bName + "' attached with " + b.getFixtureList().size + " fixtures.");
            for (com.badlogic.gdx.physics.box2d.Fixture fx : b.getFixtureList()) {
                if (fx.getShape() instanceof com.badlogic.gdx.physics.box2d.PolygonShape) {
                    com.badlogic.gdx.physics.box2d.PolygonShape ps = (com.badlogic.gdx.physics.box2d.PolygonShape) fx.getShape();
                    if (ps.getVertexCount() == 4) {
                        Vector2 v0 = new Vector2();
                        Vector2 v2 = new Vector2();
                        ps.getVertex(0, v0);
                        ps.getVertex(2, v2);
                        if (Math.abs(v0.x - (-1f)) < 0.01f && Math.abs(v2.x - 1f) < 0.01f) {
                            throw new RuntimeException("ERROR: Fallback ghost box detected on body " + bName);
                        }
                    }
                }
            }
        }
        System.out.println("\nALL 13 BODIES VALIDATED: ZERO GHOST BOXES!");
    }
}
