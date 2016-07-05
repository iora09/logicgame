package com.bob.game.inputs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.bob.game.Layer;

public abstract class Target {

    protected Image containImage;
    protected Object object;

    private DragAndDrop.Target target;
    protected int targetX;
    protected int targetY;
    protected InputsLayer layer;
    protected Skin skin;

    public void initView(Layer layer, int startingX, int startingY, Actor bkgImage, final Skin skin) {

        this.layer = (InputsLayer)layer;
        this.skin = skin;

        targetX = startingX;
        targetY = startingY;


        layer.addActor(bkgImage);

        target = new DragAndDrop.Target(bkgImage) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.GREEN);
                return true;
            }

            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.CLEAR);
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                setObject(payload.getObject());
                setImage(true);
            }
        };

        target.getActor().setColor(Color.CLEAR);
    }

    public DragAndDrop.Target getTarget() {
        return target;
    }

    public void reset() {
        if (containImage != null) {
            containImage.remove();
        }
        object = null;
    }

    public void setObject(Object object) {
        reset();
        this.object = object;
    }

    protected void setMoveAbility() {
        containImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if( getTapCount() == 2) {
                    reset();
                }
            }
        });

        DragAndDrop dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragActorPosition(-(containImage.getWidth()/2), containImage.getHeight()/2);
        dragAndDrop.addSource(new DragAndDrop.Source(containImage) {
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(object);
                payload.setDragActor(containImage);
                object = null;

                return payload;
            }
        });

        for (DragAndDrop.Target t: layer.getTargets()) {
            dragAndDrop.addTarget(t);
        }
    }

    public abstract void setImage(boolean isDragable);
}
