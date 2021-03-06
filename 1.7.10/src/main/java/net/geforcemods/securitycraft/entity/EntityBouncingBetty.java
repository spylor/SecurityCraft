package net.geforcemods.securitycraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityBouncingBetty extends Entity {

	/** How long the fuse is */
	public int fuse;

	public EntityBouncingBetty(World par1World){
		super(par1World);
		preventEntitySpawning = true;
		setSize(0.500F, 0.200F);
		yOffset = height / 2.0F;
	}

	public EntityBouncingBetty(World par1World, double par2, double par4, double par6){
		this(par1World);
		setPosition(par2, par4, par6);
		float f = (float)(Math.random() * Math.PI * 2.0D);
		motionX = -((float)Math.sin(f)) * 0.02F;
		motionY = 0.20000000298023224D;
		motionZ = -((float)Math.cos(f)) * 0.02F;
		fuse = 80;
		prevPosX = par2;
		prevPosY = par4;
		prevPosZ = par6;
	}

	@Override
	protected void entityInit() {}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.03999999910593033D;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (onGround)
		{
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
			motionY *= -0.5D;
		}

		if (fuse-- <= 0)
		{
			setDead();

			if (!worldObj.isRemote)
				explode();
		}
		else
			worldObj.spawnParticle("smoke", posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
	}

	private void explode()
	{
		float f = 6.0F;

		if(mod_SecurityCraft.configHandler.smallerMineExplosion)
			worldObj.createExplosion(this, posX, posY, posZ, (f / 2), true);
		else
			worldObj.createExplosion(this, posX, posY, posZ, f, true);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		par1NBTTagCompound.setByte("Fuse", (byte)fuse);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		fuse = par1NBTTagCompound.getByte("Fuse");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

}
