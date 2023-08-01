package ink.huix.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin extends ScreenHandler {
	@Shadow
	@Final
	private Random random;
	@Shadow
	@Final
	private Property seed;
	@Shadow
	@Final
	private Inventory inventory;
	@Shadow
	public boolean canUse(PlayerEntity player) {
		return false;
	}
	protected EnchantmentScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
	ordinal = 0),
			method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;I)V")
	private Slot injectCanInsert(EnchantmentScreenHandler instance, Slot slot){
		return this.addSlot(new Slot(this.inventory, 0, 15, 47){

			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isEnchantable() || stack.getItem() == Items.GOLDEN_APPLE;
			}

			@Override
			public int getMaxItemCount() {
				return 1;
			}
		});
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V", shift = At.Shift.AFTER)
			, method = "onContentChanged")
	private void injectChangedSeed(Inventory inventory, CallbackInfo ci){
		this.addProperty(this.seed).set(this.random.nextInt());
	}
}