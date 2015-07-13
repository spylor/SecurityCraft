package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.interfaces.IPasswordProtected;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class ItemAdminTool extends Item implements IHelpInfo {

	public ItemAdminTool() {
		super();
		
		if(mod_SecurityCraft.configHandler.allowAdminTool){
			this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
		}
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getTileEntity(pos) != null){
				if(!mod_SecurityCraft.configHandler.allowAdminTool){ 
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "The admin tool has been disabled in the config file.", EnumChatFormatting.RED); 
					return false; 
				}
				
				TileEntity te = par3World.getTileEntity(pos);
				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Block info:", EnumChatFormatting.GRAY);
				
				if(te instanceof TileEntityOwnable){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Owner: " + (((TileEntityOwnable) te).getOwnerName() == null ? "????" : ((TileEntityOwnable) te).getOwnerName()), null);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Owner's UUID: " + (((TileEntityOwnable) te).getOwnerUUID() == null ? "????" : ((TileEntityOwnable) te).getOwnerUUID()), null);
				}else if(te instanceof TileEntityKeypadChest){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Owner: " + (((TileEntityKeypadChest) te).getOwnerName() == null ? "????" : ((TileEntityKeypadChest) te).getOwnerName()), null);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Owner's UUID: " + (((TileEntityKeypadChest) te).getOwnerUUID() == null ? "????" : ((TileEntityKeypadChest) te).getOwnerUUID()), null);
				}
				
				if(te instanceof IPasswordProtected){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Password: " + (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword()), null);
				}
				
				if(te instanceof CustomizableSCTE){
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();
					
					if(!modules.isEmpty()){
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Equipped modules: ", null);
						
						for(EnumCustomModules module : modules){
							PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "-" + module.getModuleName(), null);
						}
					}
				}
				
				return true;
			}
		}
		
		return false;
	}

	public String getHelpInfo() {
		return null;
	}

	public String[] getRecipe() {
		return null;
	}

}