package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.containers.ContainerCustomizeBlock;
import org.freeforums.geforce.securitycraft.containers.ContainerInventoryScanner;
import org.freeforums.geforce.securitycraft.containers.ContainerKeycardSetup;
import org.freeforums.geforce.securitycraft.containers.ContainerKeypad;
import org.freeforums.geforce.securitycraft.containers.ContainerKeypadChest;
import org.freeforums.geforce.securitycraft.containers.ContainerKeypadChestSetup;
import org.freeforums.geforce.securitycraft.containers.ContainerKeypadSetup;
import org.freeforums.geforce.securitycraft.containers.ContainerLogger;
import org.freeforums.geforce.securitycraft.containers.ContainerRAMActivate;
import org.freeforums.geforce.securitycraft.containers.ContainerRAMDeactivate;
import org.freeforums.geforce.securitycraft.containers.ContainerRAMDetonate;
import org.freeforums.geforce.securitycraft.containers.ContainerRemoteAccessMine;
import org.freeforums.geforce.securitycraft.containers.ContainerRetinalScanner;
import org.freeforums.geforce.securitycraft.containers.ContainerRetinalScannerSetup;
import org.freeforums.geforce.securitycraft.containers.ContainerSecurityCamera;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLogger;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRAM;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	public static final int amountOfGUIs = 13;

	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile_entity = world.getTileEntity(x, y, z);
		
		if(tile_entity instanceof TileEntitySCTE || tile_entity instanceof TileEntityKeypadChest || tile_entity instanceof TileEntityInventoryScanner || ID == 5 || ID == 6 || ID == 7 || ID == 8 || ID == 14){
			//Is securitycraft TE.
		}else{
			//Is not securitycraft TE.
			return null;
		}

    	switch(ID){
    	case 0:
          return new ContainerKeypad(player.inventory, (TileEntityKeypad) tile_entity);
          
    	case 1:
            return new ContainerKeypadSetup(player.inventory, (TileEntityKeypad) tile_entity);
        
    	case 2:
    		return new ContainerRetinalScannerSetup(player.inventory, (TileEntityOwnable) tile_entity);
    	
    	case 3:
    		return new ContainerRetinalScanner(player.inventory, (TileEntityOwnable) tile_entity);
    	
    	case 4:
    		return new ContainerKeycardSetup(player.inventory, (TileEntityKeycardReader) tile_entity);
    	
    	case 5:
    		return new ContainerRemoteAccessMine(player.inventory, (TileEntityRAM) tile_entity);
    	
    	case 6:
    		return new ContainerRAMActivate(player.inventory, (TileEntityRAM) tile_entity);
    	
    	case 7:
    		return new ContainerRAMDeactivate(player.inventory, (TileEntityRAM) tile_entity);
    	
    	case 8:
    		return new ContainerRAMDetonate(player.inventory, (TileEntityRAM) tile_entity);

    	case 9:
    		return new ContainerInventoryScanner(player.inventory, (TileEntityInventoryScanner) tile_entity);
    	
    	case 10:
    		return new ContainerSecurityCamera(player.inventory, (TileEntitySecurityCamera) tile_entity);
    		
    	case 11:
    		return new ContainerLogger(player.inventory, (TileEntityLogger) tile_entity);
    	
    	case 12:
    		return new ContainerKeypadChestSetup(player.inventory, (TileEntityKeypadChest) tile_entity);
    		
    	case 13:
    		return new ContainerKeypadChest(player.inventory, (TileEntityKeypadChest) tile_entity);
    	
    		
    	case 100:
        	return new ContainerCustomizeBlock(player.inventory, (CustomizableSCTE) tile_entity);
    	
    	default:
        	return null;
    	}
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile_entity = world.getTileEntity(x, y, z);
		
		if((tile_entity instanceof TileEntitySCTE || tile_entity instanceof TileEntityKeypadChest) || tile_entity instanceof TileEntityInventoryScanner || ID == 5 || ID == 6 || ID == 7 || ID == 8 || ID == 14){
			//Is securitycraft TE.
		}else{
			//Is not securitycraft TE.
			return null;
		}

		switch(ID){
    	case 0:
          return new GuiKeypad(player.inventory, (TileEntityKeypad) tile_entity);
  
    	case 1:
            return new GuiKeypadSetup(player.inventory, (TileEntityKeypad) tile_entity);
    	
    	case 2:
    		return new GuiRetinalScannerSetup(player.inventory, (TileEntityOwnable) tile_entity);
    	
    	case 3:
    		return new GuiRetinalScanner(player.inventory, (TileEntityOwnable) tile_entity);
    	
    	case 4:
    		return new GuiKeycardSetup(player.inventory, (TileEntityKeycardReader) tile_entity);
    	
    	case 5:
    		return new GuiRemoteAccessMine(player.inventory, (TileEntityRAM) tile_entity);
    		
    	case 6:
    		return new GuiRAMActivate(player.inventory, (TileEntityRAM) tile_entity, player.getCurrentEquippedItem());
    	
    	case 7:
    		return new GuiRAMDeactivate(player.inventory, (TileEntityRAM) tile_entity, player.getCurrentEquippedItem());
    	
    	case 8:
    		return new GuiRAMDetonate(player.inventory, (TileEntityRAM) tile_entity, player.getCurrentEquippedItem());
    	
    	case 9:
    		return new GuiInventoryScanner(player.inventory, (TileEntityInventoryScanner) tile_entity, player);
    	
    	case 10:
    		return new GuiSecurityCamera(player.inventory, (TileEntitySecurityCamera) tile_entity);
    	
    	case 11:
    		return new GuiLogger(player.inventory, (TileEntityLogger) tile_entity);
    	
    	case 12:
    		return new GuiKeypadChestSetup(player.inventory, (TileEntityKeypadChest) tile_entity);
    		
    	case 13:
    		return new GuiKeypadChest(player.inventory, (TileEntityKeypadChest) tile_entity);
    		
    		
    	case 100:
    		return new GuiCustomizeBlock(player.inventory, (CustomizableSCTE) tile_entity);
    		
    	default:
        	return null;
	}
	}

}
