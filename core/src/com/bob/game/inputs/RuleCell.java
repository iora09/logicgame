package com.bob.game.inputs;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;

class RuleCell extends Target{

    public RuleCell() {

    }

    public String getLPSString() {
        return payload != null ? ((Block) payload).getLPSString() : "";
    }

    public Type getType() {
        return payload != null ? ((Block) payload).getType() : null;
    }

    @Override
    public void setImage(boolean isDragable) {
        if (payload != null) {
            actor = new Image(skin, ((Block) payload).getImageName());
            actor.setBounds(targetX, targetY, 50, 50);
            layer.addActor(actor);

            TextTooltip tooltip = new TextTooltip(((Block) payload).getTooltip(), skin);
            tooltip.setInstant(true);
            actor.addListener(tooltip);

            if (isDragable) {
                setMoveAbility();
            }
        }
    }

    public Block getBlock() {
        return ((Block) payload);
    }
}
