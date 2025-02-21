package me.polishkrowa.politweaks.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrownedEntity.class)
public class DrownedEntityMixin extends MobEntity {
    public DrownedEntityMixin(EntityType<? extends DrownedEntity> entityType, World world) {
        super(entityType, world);
    }


//    @Inject(method = "initEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextFloat()F"), cancellable = true)
//    private static void injected(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
//        if (!((random.nextFloat() >= 0.1D && localDifficulty.isAtLeastHard()) || random.nextFloat() > 0.8D)) {
//            ci.cancel();
//        }
//
//    }

    @Inject(method = "initEquipment", at = @At(value = "HEAD"), cancellable = true)
    private void injected(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        if ((localDifficulty.isAtLeastHard() && (double)this.random.nextFloat() >= 0.1D) || (double)this.random.nextFloat() > 0.8D) {
            int i = this.random.nextInt(localDifficulty.isAtLeastHard() ? 10 : 13);
            if (i < 10) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }
        ci.cancel();
    }


    //        if ((double)random.nextFloat() > 0.9) {
    //            int i = random.nextInt(16);
    //            if (i < 10) {
    //                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
    //            } else {
    //                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
    //            }
    //
    //       }

}
