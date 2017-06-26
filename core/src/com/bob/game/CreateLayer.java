package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.Group;

public class CreateLayer extends Layer {
    private Group creationGroup = new Group();

    public void setCreationGroup(Group group) {
        this.creationGroup = group;
    }

    public Group getCreationGroup() {
        return creationGroup;
    }

    @Override
    public void setVisibility(boolean visible) {
        super.setVisibility(visible);
        creationGroup.setVisible(visible);
    }

    public void reset() {
        creationGroup.clear();
        group.clear();
    }
}
