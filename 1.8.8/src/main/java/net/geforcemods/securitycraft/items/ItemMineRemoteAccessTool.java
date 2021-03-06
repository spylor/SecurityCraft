package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMineRemoteAccessTool extends Item {

	public int listIndex = 0;

	public ItemMineRemoteAccessTool() {
		super();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
		if(par2World.isRemote)
			return par1ItemStack;
		else{
			par3EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, par2World, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);
			return par1ItemStack;
		}
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing par5EnumFacing, float hitX, float hitY, float hitZ){
		if(!par3World.isRemote)
			if(BlockUtils.getBlock(par3World, pos) instanceof IExplosive){
				if(!isMineAdded(par1ItemStack, par3World, pos)){
					int availSlot = getNextAvaliableSlot(par1ItemStack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.noSlots"), EnumChatFormatting.RED);
						return false;
					}

					if(par3World.getTileEntity(pos) instanceof IOwnable && !((IOwnable) par3World.getTileEntity(pos)).getOwner().isOwner(par2EntityPlayer)){
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.cantBind"), EnumChatFormatting.RED);
						return false;
					}

					if(par1ItemStack.getTagCompound() == null)
						par1ItemStack.setTagCompound(new NBTTagCompound());

					par1ItemStack.getTagCompound().setIntArray(("mine" + availSlot), new int[]{BlockUtils.fromPos(pos)[0], BlockUtils.fromPos(pos)[1], BlockUtils.fromPos(pos)[2]});
					mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par2EntityPlayer);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.bound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(par1ItemStack, pos, par2EntityPlayer);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.unbound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.RED);
				}
			}
			else
				par2EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, par3World, (int) par2EntityPlayer.posX, (int) par2EntityPlayer.posY, (int) par2EntityPlayer.posZ);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					par3List.add("---");
					continue;
				}
				else
					par3List.add(StatCollector.translateToLocal("tooltip.mine") + " " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]);
			}
			else
				par3List.add("---");
	}

	private void removeTagFromItemAndUpdate(ItemStack par1ItemStack, BlockPos pos, EntityPlayer par5EntityPlayer) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					par1ItemStack.getTagCompound().setIntArray("mine" + i, new int[]{0, 0, 0});
					mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par5EntityPlayer);
					return;
				}
			}
			else
				continue;


		return;
	}

	private boolean isMineAdded(ItemStack par1ItemStack, World par2World, BlockPos pos) {
		if(par1ItemStack.getTagCompound() == null)
			return false;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;


		return false;
	}

	private int getNextAvaliableSlot(ItemStack par1ItemStack){
		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound() == null)
				return 1;
			else if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length == 0 || (par1ItemStack.getTagCompound().getIntArray("mine" + i)[0] == 0 && par1ItemStack.getTagCompound().getIntArray("mine" + i)[1] == 0 && par1ItemStack.getTagCompound().getIntArray("mine" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}

}
