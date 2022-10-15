package ovh.roro.parkour.asm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.init.Blocks;

public class BlockPanePatch {

    public static boolean canPaneConnectToBlock(Block block) {
        if (!"minecraft".equals(Block.blockRegistry.getNameForObject(block).getResourceDomain())) {
            return true;
        }

        return block.isFullBlock() || block == Blocks.glass || block == Blocks.stained_glass || block == Blocks.stained_glass_pane || block instanceof BlockPane;
    }
}
