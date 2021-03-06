package com.bob.game.inputs;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;

public class RuleCell extends Target{

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
            layer.addActorFirst(actor);

            TextTooltip tooltip = new TextTooltip(((Block) payload).getTooltip(), skin, "tooltipStyle");
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
