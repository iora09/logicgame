package com.bob.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class Layer {
    protected final Group group = new Group();
    private Group creationGroup = new Group();
    protected boolean initialVisibility = true;
    private boolean visibility = false;

    public void setInitialVisibility() {
        setVisibility(initialVisibility);
    }

    public void setVisibility(boolean visible) {
        group.setVisible(visible);
        creationGroup.setVisible(visible);
        visibility = visible;
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setStage(Stage stage) {
        stage.addActor(group);
    }

    public void addActor(Actor actor) {
        group.addActor(actor);
    }

    public void addActorAt(int index, Actor actor) {
        group.addActorAt(index, actor);
    }

    public void addActorFirst(Actor actor) {
        group.addActorAt(0,actor);
    }

    public void removeActor(Actor actor) {
        group.removeActor(actor);
    }

    public void setOpacity(float opacity) {
        Color color = group.getColor();
        color.a = opacity;

        group.setColor(color);
    }

    public void setCreationGroup(Group group) {
        this.creationGroup = group;
    }

    public Group getCreationGroup() {
        return creationGroup;
    }
}
