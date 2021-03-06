package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityKeypadFurnace extends TileEntityOwnable implements ISidedInventory, IPasswordProtected {

	private static final int[] slotsTop = new int[] {0};
	private static final int[] slotsBottom = new int[] {2, 1};
	private static final int[] slotsSides = new int[] {1};
	public ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int cookTime;
	public int totalCookTime;
	private String furnaceCustomName;
	private String passcode;

	@Override
	public int getSizeInventory()
	{
		return furnaceItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return furnaceItemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (furnaceItemStacks[index] != null)
		{
			ItemStack itemstack;

			if (furnaceItemStacks[index].stackSize <= count)
			{
				itemstack = furnaceItemStacks[index];
				furnaceItemStacks[index] = null;
				return itemstack;
			}
			else
			{
				itemstack = furnaceItemStacks[index].splitStack(count);

				if (furnaceItemStacks[index].stackSize == 0)
					furnaceItemStacks[index] = null;

				return itemstack;
			}
		}
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index)
	{
		if (furnaceItemStacks[index] != null)
		{
			ItemStack itemstack = furnaceItemStacks[index];
			furnaceItemStacks[index] = null;
			return itemstack;
		}
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		boolean flag = stack != null && stack.isItemEqual(furnaceItemStacks[index]) && ItemStack.areItemStackTagsEqual(stack, furnaceItemStacks[index]);
		furnaceItemStacks[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		if (index == 0 && !flag)
		{
			totalCookTime = func_174904_a(stack);
			cookTime = 0;
			markDirty();
		}
	}

	@Override
	public String getCommandSenderName()
	{
		return hasCustomName() ? furnaceCustomName : "container.furnace";
	}

	@Override
	public boolean hasCustomName()
	{
		return furnaceCustomName != null && furnaceCustomName.length() > 0;
	}

	public void setCustomInventoryName(String p_145951_1_)
	{
		furnaceCustomName = p_145951_1_;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		furnaceItemStacks = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < furnaceItemStacks.length)
				furnaceItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}

		furnaceBurnTime = compound.getShort("BurnTime");
		cookTime = compound.getShort("CookTime");
		totalCookTime = compound.getShort("CookTimeTotal");
		currentItemBurnTime = getItemBurnTime(furnaceItemStacks[1]);
		passcode = compound.getString("passcode");

		if (compound.hasKey("CustomName", 8))
			furnaceCustomName = compound.getString("CustomName");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setShort("BurnTime", (short)furnaceBurnTime);
		compound.setShort("CookTime", (short)cookTime);
		compound.setShort("CookTimeTotal", (short)totalCookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < furnaceItemStacks.length; ++i)
			if (furnaceItemStacks[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				furnaceItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}

		compound.setTag("Items", nbttaglist);

		if(passcode != null && !passcode.isEmpty())
			compound.setString("passcode", passcode);

		if (hasCustomName())
			compound.setString("CustomName", furnaceCustomName);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	 * cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int p_145953_1_)
	{
		return cookTime * p_145953_1_ / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int p_145955_1_)
	{
		if (currentItemBurnTime == 0)
			currentItemBurnTime = 200;

		return furnaceBurnTime * p_145955_1_ / currentItemBurnTime;
	}

	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	@SideOnly(Side.CLIENT)
	public static boolean isBurning(IInventory p_174903_0_)
	{
		return p_174903_0_.getField(0) > 0;
	}

	@Override
	public void update()
	{
		boolean flag = this.isBurning();
		boolean flag1 = false;

		//        if(this.isBurning() && !((Boolean) Utils.getBlockProperty(getWorld(), getPos(), BlockKeypadFurnace.COOKING)).booleanValue()){
		//        	Utils.setBlockProperty(getWorld(), getPos(), BlockKeypadFurnace.COOKING, true);
		//        }else if(!this.isBurning() && ((Boolean) Utils.getBlockProperty(getWorld(), getPos(), BlockKeypadFurnace.COOKING)).booleanValue()){
		//        	Utils.setBlockProperty(getWorld(), getPos(), BlockKeypadFurnace.COOKING, false);
		//        }

		if (this.isBurning())
			--furnaceBurnTime;

		if (!worldObj.isRemote)
		{
			if (!this.isBurning() && (furnaceItemStacks[1] == null || furnaceItemStacks[0] == null))
			{
				if (!this.isBurning() && cookTime > 0)
					cookTime = MathHelper.clamp_int(cookTime - 2, 0, totalCookTime);
			}
			else
			{
				if (!this.isBurning() && canSmelt())
				{
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(furnaceItemStacks[1]);

					if (this.isBurning())
					{
						flag1 = true;

						if (furnaceItemStacks[1] != null)
						{
							--furnaceItemStacks[1].stackSize;

							if (furnaceItemStacks[1].stackSize == 0)
								furnaceItemStacks[1] = furnaceItemStacks[1].getItem().getContainerItem(furnaceItemStacks[1]);
						}
					}
				}

				if (this.isBurning() && canSmelt())
				{
					++cookTime;

					if (cookTime == totalCookTime)
					{
						cookTime = 0;
						totalCookTime = func_174904_a(furnaceItemStacks[0]);
						smeltItem();
						flag1 = true;
					}
				}
				else
					cookTime = 0;
			}

			if (flag != this.isBurning())
				flag1 = true;
		}

		if (flag1)
			markDirty();
	}

	public int func_174904_a(ItemStack p_174904_1_)
	{
		return 200;
	}

	private boolean canSmelt()
	{
		if (furnaceItemStacks[0] == null)
			return false;
		else
		{
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);
			if (itemstack == null) return false;
			if (furnaceItemStacks[2] == null) return true;
			if (!furnaceItemStacks[2].isItemEqual(itemstack)) return false;
			int result = furnaceItemStacks[2].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	public void smeltItem()
	{
		if (canSmelt())
		{
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);

			if (furnaceItemStacks[2] == null)
				furnaceItemStacks[2] = itemstack.copy();
			else if (furnaceItemStacks[2].getItem() == itemstack.getItem())
				furnaceItemStacks[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items

			if (furnaceItemStacks[0].getItem() == Item.getItemFromBlock(Blocks.sponge) && furnaceItemStacks[0].getMetadata() == 1 && furnaceItemStacks[1] != null && furnaceItemStacks[1].getItem() == Items.bucket)
				furnaceItemStacks[1] = new ItemStack(Items.water_bucket);

			--furnaceItemStacks[0].stackSize;

			if (furnaceItemStacks[0].stackSize <= 0)
				furnaceItemStacks[0] = null;
		}
	}

	public static int getItemBurnTime(ItemStack p_145952_0_)
	{
		if (p_145952_0_ == null)
			return 0;
		else
		{
			Item item = p_145952_0_.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air)
			{
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab)
					return 150;

				if (block.getMaterial() == Material.wood)
					return 300;

				if (block == Blocks.coal_block)
					return 16000;
			}

			if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
			if (item == Items.stick) return 100;
			if (item == Items.coal) return 1600;
			if (item == Items.lava_bucket) return 20000;
			if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
			if (item == Items.blaze_rod) return 2400;
			return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(p_145952_0_);
		}
	}

	public static boolean isItemFuel(ItemStack p_145954_0_)
	{
		return getItemBurnTime(p_145954_0_) > 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return index == 2 ? false : (index != 1 ? true : isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		if (direction == EnumFacing.DOWN && index == 1)
		{
			Item item = stack.getItem();

			if (item != Items.water_bucket && item != Items.bucket)
				return false;
		}

		return true;
	}

	public String getGuiID()
	{
		return "minecraft:furnace";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new ContainerFurnace(playerInventory, this);
	}

	@Override
	public int getField(int id)
	{
		switch (id)
		{
			case 0:
				return furnaceBurnTime;
			case 1:
				return currentItemBurnTime;
			case 2:
				return cookTime;
			case 3:
				return totalCookTime;
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
			case 0:
				furnaceBurnTime = value;
				break;
			case 1:
				currentItemBurnTime = value;
				break;
			case 2:
				cookTime = value;
				break;
			case 3:
				totalCookTime = value;
		}
	}

	@Override
	public int getFieldCount()
	{
		return 4;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < furnaceItemStacks.length; ++i)
			furnaceItemStacks[i] = null;
	}

	@Override
	public IChatComponent getDisplayName() {
		return hasCustomName() ? new ChatComponentText(getCommandSenderName()) : new ChatComponentTranslation(getCommandSenderName(), new Object[0]);
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadFurnace)
			BlockKeypadFurnace.activate(worldObj, pos, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
			player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
		else
			player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypadFurnace.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
		else {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

}
