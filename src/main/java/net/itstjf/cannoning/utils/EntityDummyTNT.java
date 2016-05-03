package net.itstjf.cannoning.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.itstjf.cannoning.mods.Projection;
import net.itstjf.cannoning.mods.Projection.Twiangle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public class EntityDummyTNT extends Entity {
	public EntityDummyTNT(World worldIn) {
		super(worldIn);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
	}
	
	public EntityDummyTNT(World worldIn, double x, double y, double z) {
		this(worldIn);
		this.setPosition(x, y, z);
		float piRand = (float) (Math.random() * Math.PI * 2.0D);
		this.motionX = (double) (-((float) Math.sin((double) piRand)) * 0.02F);
		this.motionY = 0.20000000298023224D;
		this.motionZ = (double) (-((float) Math.cos((double) piRand)) * 0.02F);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}
	
	protected void entityInit() {}
	
	protected boolean canTriggerWalking() {
		return false;
	}
	
	public boolean canBeCollidedWith() {
		return true;
	}
	
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;
		
		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
		}
		
		this.handleWaterMovement();
	}
	
	public void moveEntity(double x, double y, double z) {
		if (this.noClip) {
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
			this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
			this.posY = this.getEntityBoundingBox().minY;
			this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
		} else {
			this.worldObj.theProfiler.startSection("move");
			
			double xPosCopy = this.posX;
			double yPosCopy = this.posY;
			double zPosCopy = this.posZ;
			
			if (this.isInWeb) {
				this.isInWeb = false;
				x *= 0.25D;
				y *= 0.05000000074505806D;
				z *= 0.25D;
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}
			
			double xCopy = x;
			double yCopy = y;
			double zCopy = z;
			
			List<AxisAlignedBB> collidedBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x, y, z));
			AxisAlignedBB boundingBox = this.getEntityBoundingBox();
			AxisAlignedBB newAxisV;
			
			for (Iterator<AxisAlignedBB> itrV = collidedBoxes.iterator(); itrV.hasNext(); y = newAxisV.calculateYOffset(this.getEntityBoundingBox(), y)) {
				newAxisV = itrV.next();
			}
			
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
			AxisAlignedBB newAxisH;
			Iterator<AxisAlignedBB> itrH;
			
			for (itrH = collidedBoxes.iterator(); itrH.hasNext(); x = newAxisH.calculateXOffset(this.getEntityBoundingBox(), x)) {
				newAxisH = itrH.next();
			}
			
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
			
			for (itrH = collidedBoxes.iterator(); itrH.hasNext(); z = newAxisH.calculateZOffset(this.getEntityBoundingBox(), z)) {
				newAxisH = itrH.next();
			}
			
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));
			
			this.worldObj.theProfiler.endSection();
			this.worldObj.theProfiler.startSection("rest");
			
			this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
			this.posY = this.getEntityBoundingBox().minY;
			this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
			
			this.isCollidedHorizontally = xCopy != x || zCopy != z;
			this.isCollidedVertically = yCopy != y;
			this.onGround = this.isCollidedVertically && yCopy < 0.0D;
			this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
			
			int xFloor = MathHelper.floor_double(this.posX);
			int yFloor = MathHelper.floor_double(this.posY - 0.20000000298023224D);
			int zFloor = MathHelper.floor_double(this.posZ);
			
			BlockPos blockPos = new BlockPos(xFloor, yFloor, zFloor);
			Block block = this.worldObj.getBlockState(blockPos).getBlock();
			
			if (block.getMaterial() == Material.air) {
				Block blockDown = this.worldObj.getBlockState(blockPos.down()).getBlock();
				
				if (blockDown instanceof BlockFence || blockDown instanceof BlockWall || blockDown instanceof BlockFenceGate) {
					block = blockDown;
					blockPos = blockPos.down();
				}
			}
			
			this.updateFallState(y, this.onGround, block, blockPos);
			
			if (xCopy != x) {
				this.motionX = 0.0D;
			}
			
			if (zCopy != z) {
				this.motionZ = 0.0D;
			}
			
			if (yCopy != y) {
				block.onLanded(this.worldObj, this);
			}
			
			this.worldObj.theProfiler.endSection();
		}
	}
	
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {}
	
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {}
	
	public float getEyeHeight() { return 0.0F; }
}