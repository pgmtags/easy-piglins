package de.maxhenkel.easypiglins.items;

import de.maxhenkel.corelib.client.CustomRendererItem;
import de.maxhenkel.corelib.client.ItemRenderer;
import de.maxhenkel.easypiglins.Main;
import de.maxhenkel.easypiglins.datacomponents.PiglinData;
import de.maxhenkel.easypiglins.items.render.PiglinItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class PiglinItem extends CustomRendererItem {

    private String translationKey;

    public PiglinItem() {
        super(new Item.Properties().stacksTo(1));
        translationKey = EntityType.PIGLIN.getDescriptionId();

        DispenserBlock.registerBehavior(this, (source, stack) -> {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.pos().relative(direction);
            Level world = source.level();
            Piglin piglin = PiglinData.createPiglin(stack, world);
            piglin.absMoveTo(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, direction.toYRot(), 0F);
            world.addFreshEntity(piglin);
            stack.shrink(1);
            return stack;
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemRenderer createItemRenderer() {
        return new PiglinItemRenderer();
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return translationKey;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);

            if (!blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos = blockpos.relative(direction);
            }

            Piglin piglin = PiglinData.createPiglin(itemstack, world);

            piglin.setPos(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5);

            if (world.addFreshEntity(piglin)) {
                itemstack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getName(ItemStack stack) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {
            return PiglinData.getCachePiglin(stack, world).getDisplayName();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (!(entity instanceof Player) || world.isClientSide) {
            return;
        }
        if (!Main.SERVER_CONFIG.piglinInventorySounds.get()) {
            return;
        }
        if (world.getGameTime() % 20 != 0) {
            return;
        }
        if (world.random.nextInt(20) == 0) {
            Player playerEntity = (Player) entity;
            playerEntity.playNotifySound(SoundEvents.PIGLIN_AMBIENT, SoundSource.HOSTILE, 1F, 1F);
        }
    }

}
