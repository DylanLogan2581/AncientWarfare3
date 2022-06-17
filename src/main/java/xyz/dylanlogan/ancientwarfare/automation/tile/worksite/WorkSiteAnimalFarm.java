package xyz.dylanlogan.ancientwarfare.automation.tile.worksite;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import xyz.dylanlogan.ancientwarfare.core.inventory.ItemSlotFilter;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.core.util.InventoryTools;

import java.util.ArrayList;
import java.util.List;

public class WorkSiteAnimalFarm extends TileWorksiteBoundedInventory {

    private static final int TOP_LENGTH = 27, FRONT_LENGTH = 3, BOTTOM_LENGTH = 3;
    private int workerRescanDelay;
    private boolean shouldCountResources;

    public int maxPigCount = 6;
    public int maxCowCount = 6;
    public int maxChickenCount = 6;
    public int maxSheepCount = 6;

    private int wheatCount;
    private int bucketCount;
    private int carrotCount;
    private int seedCount;
    private ItemStack shears = null;

    private List<EntityPair> pigsToBreed = new ArrayList<EntityPair>();
    private List<EntityPair> chickensToBreed = new ArrayList<EntityPair>();
    private List<EntityPair> cowsToBreed = new ArrayList<EntityPair>();
    private int cowsToMilk;
    private List<EntityPair> sheepToBreed = new ArrayList<EntityPair>();
    private List<Integer> sheepToShear = new ArrayList<Integer>();
    private List<Integer> entitiesToCull = new ArrayList<Integer>();

    public WorkSiteAnimalFarm() {
        this.shouldCountResources = true;

        this.inventory = new InventorySided(this, RotationType.FOUR_WAY, TOP_LENGTH + FRONT_LENGTH + BOTTOM_LENGTH) {
            @Override
            public void markDirty() {
                super.markDirty();
                shouldCountResources = true;
            }
        };
        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        int[] bottomIndices = helper.getIndiceArrayForSpread(BOTTOM_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//feed
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//buckets/shears
        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack == null || isFood(stack.getItem());
            }
        };
        inventory.setFilterForSlots(filter, frontIndices);

        filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack == null || isTool(stack.getItem());
            }
        };
        inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isFood(Item item){
        return item == Items.wheat_seeds || item == Items.wheat || item == Items.carrot;
    }

    private boolean isTool(Item item){
        return item == Items.bucket || item instanceof ItemShears;
    }

    @Override
    public boolean userAdjustableBlocks() {
        return false;
    }

    @Override
    protected boolean hasWorksiteWork() {
        return !entitiesToCull.isEmpty()
                || (carrotCount > 0 && !pigsToBreed.isEmpty())
                || (seedCount > 0 && !chickensToBreed.isEmpty())
                || (wheatCount > 0 && (!cowsToBreed.isEmpty() || !sheepToBreed.isEmpty()))
                || (bucketCount > 0 && cowsToMilk > 0)
                || (shears != null && !sheepToShear.isEmpty());
    }

    @Override
    protected void updateWorksite() {
        worldObj.theProfiler.startSection("Count Resources");
        if (shouldCountResources) {
            countResources();
            this.shouldCountResources = false;
        }
        worldObj.theProfiler.endStartSection("Animal Rescan");
        if (workerRescanDelay-- <= 0) {
            rescan();
            workerRescanDelay = 200;
        }
        worldObj.theProfiler.endStartSection("EggPickup");
        if (worldObj.getWorldTime() % 20 == 0) {
            pickupEggs();
        }
        worldObj.theProfiler.endSection();
    }

    private void countResources() {
        carrotCount = 0;
        seedCount = 0;
        wheatCount = 0;
        bucketCount = 0;
        shears = null;
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Items.carrot) {
                carrotCount += stack.stackSize;
            } else if (stack.getItem() == Items.wheat_seeds) {
                seedCount += stack.stackSize;
            } else if (stack.getItem() == Items.wheat) {
                wheatCount += stack.stackSize;
            }
        }
        for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Items.bucket) {
                bucketCount += stack.stackSize;
            } else if (stack.getItem() instanceof ItemShears) {
                shears = stack;
            }
        }
//  AWLog.logDebug("counting animal farm resources.."+wheatCount+","+seedCount+","+carrotCount+","+bucketCount+","+shears);
    }

    @SuppressWarnings("unchecked")
    private void rescan() {
//  AWLog.logDebug("rescanning animal farm");
        pigsToBreed.clear();
        cowsToBreed.clear();
        cowsToMilk = 0;
        sheepToBreed.clear();
        chickensToBreed.clear();
        entitiesToCull.clear();

        List<EntityAnimal> entityList = getEntitiesWithinBounds(EntityAnimal.class);

        List<EntityAnimal> cows = new ArrayList<EntityAnimal>();
        List<EntityAnimal> pigs = new ArrayList<EntityAnimal>();
        List<EntityAnimal> sheep = new ArrayList<EntityAnimal>();
        List<EntityAnimal> chickens = new ArrayList<EntityAnimal>();

        for (EntityAnimal animal : entityList) {
            if (animal instanceof EntityCow) {
                cows.add(animal);
            } else if (animal instanceof EntityChicken) {
                chickens.add(animal);
            } else if (animal instanceof EntitySheep) {
                sheep.add(animal);
            } else if (animal instanceof EntityPig) {
                pigs.add(animal);
            }
        }

        scanForCows(cows);
        scanForSheep(sheep);
        scanForAnimals(chickens, chickensToBreed, maxChickenCount);
        scanForAnimals(pigs, pigsToBreed, maxPigCount);
    }

    private void scanForAnimals(List<EntityAnimal> animals, List<EntityPair> targets, int maxCount) {
        EntityAnimal animal1;
        EntityAnimal animal2;
        EntityPair breedingPair;

        int age;

        for (int i = 0; i < animals.size(); i++) {
            animal1 = animals.get(i);
            age = animal1.getGrowingAge();
            if (age != 0 || animal1.isInLove()) {
                continue;
            }//unbreedable first-target, skip
            while (i + 1 < animals.size())//loop through remaining animals to find a breeding partner
            {
                i++;
                animal2 = animals.get(i);
                age = animal2.getGrowingAge();
                if (age == 0 && !animal2.isInLove())//found a second breedable animal, add breeding pair, exit to outer loop
                {
                    breedingPair = new EntityPair(animal1, animal2);
                    targets.add(breedingPair);
                    break;
                }
            }
        }

        int grownCount = 0;
        for (EntityAnimal animal : animals) {
            if (animal.getGrowingAge() >= 0) {
                grownCount++;
            }
        }

        if (grownCount > maxCount) {
            for (int i = 0, cullCount = grownCount - maxCount; i < animals.size() && cullCount > 0; i++) {
                if (animals.get(i).getGrowingAge() >= 0) {
                    entitiesToCull.add(animals.get(i).getEntityId());
                    cullCount--;
                }
            }
        }
    }

    private void scanForSheep(List<EntityAnimal> sheep) {
        scanForAnimals(sheep, sheepToBreed, maxSheepCount);
        for (EntityAnimal animal : sheep) {
            if (animal.getGrowingAge() >= 0) {
                EntitySheep sheep1 = (EntitySheep) animal;
                if (!sheep1.getSheared()) {
                    sheepToShear.add(sheep1.getEntityId());
                }
            }
        }
    }

    private void scanForCows(List<EntityAnimal> animals) {
        scanForAnimals(animals, cowsToBreed, maxCowCount);
        for (EntityAnimal animal : animals) {
            if (animal.getGrowingAge() >= 0) {
                cowsToMilk++;
                if (cowsToMilk > maxCowCount) {
                    cowsToMilk = maxCowCount;
                    break;
                }
            }
        }
    }

    @Override
    protected boolean processWork() {
//  AWLog.logDebug("processing animal farm work!");

        if (!cowsToBreed.isEmpty() && wheatCount >= 2) {
            if (tryBreeding(cowsToBreed)) {
                wheatCount -= 2;
                InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat), 2);
                return true;
            }
        }
        if (!sheepToBreed.isEmpty() && wheatCount >= 2) {
            if (tryBreeding(sheepToBreed)) {
                wheatCount -= 2;
                InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat), 2);
                return true;
            }
        }
        if (!chickensToBreed.isEmpty() && seedCount >= 2) {
            if (tryBreeding(chickensToBreed)) {
                seedCount -= 2;
                InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat_seeds), 2);
                return true;
            }
        }
        if (!pigsToBreed.isEmpty() && carrotCount >= 2) {
            if (tryBreeding(pigsToBreed)) {
                carrotCount -= 2;
                InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.carrot), 2);
                return true;
            }
        }
        if (tryShearing()) {
            return true;
        }
        if (bucketCount > 0 && tryMilking()) {
            InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.BOTTOM), new ItemStack(Items.bucket), 1);
            this.addStackToInventory(new ItemStack(Items.milk_bucket), RelativeSide.TOP);
            return true;
        }
        return tryCulling();
    }

    private boolean tryBreeding(List<EntityPair> targets) {
        Entity animalA;
        Entity animalB;
        EntityPair pair;
        if (!targets.isEmpty()) {
            pair = targets.remove(0);
            animalA = pair.getEntityA(worldObj);
            animalB = pair.getEntityB(worldObj);
            if (!(animalA instanceof EntityAnimal) || !(animalB instanceof EntityAnimal)) {
                return false;
            }
            if (animalA.isEntityAlive() && animalB.isEntityAlive()) {
                ((EntityAnimal) animalA).func_146082_f(getOwnerAsPlayer());//setInLove(EntityPlayer breeder)
                ((EntityAnimal) animalB).func_146082_f(getOwnerAsPlayer());//setInLove(EntityPlayer breeder)
                return true;
            }
        }
        return false;
    }

    private boolean tryMilking() {
        return cowsToMilk > 0 && worldObj.rand.nextInt(cowsToMilk + getFortune()) > maxCowCount / 2;
    }

    private boolean tryShearing() {
        if(shears == null || sheepToShear.isEmpty()) {
            return false;
        }
        EntitySheep sheep = (EntitySheep) worldObj.getEntityByID(sheepToShear.remove(0));
        if (sheep == null || !sheep.isShearable(shears, worldObj, xCoord, yCoord, zCoord)) {
            return false;
        }
        ArrayList<ItemStack> items = sheep.onSheared(shears, worldObj, xCoord, yCoord, zCoord, getFortune());
        for (ItemStack item : items) {
            addStackToInventory(item, RelativeSide.TOP);
        }
        return true;
    }

    private boolean tryCulling() {
        Entity entity;
        EntityAnimal animal;
        int fortune = getFortune();
        while (!entitiesToCull.isEmpty()) {
            entity = worldObj.getEntityByID(entitiesToCull.remove(0));
            if (entity instanceof EntityAnimal && entity.isEntityAlive()) {
                animal = (EntityAnimal) entity;
                if (animal.isInLove() || animal.getGrowingAge() < 0) {
                    continue;
                }

                animal.captureDrops = true;
                animal.arrowHitTimer = 10;
                animal.attackEntityFrom(DamageSource.generic, animal.getHealth() + 1);
                ItemStack stack;
                for (EntityItem item : animal.capturedDrops) {
                    stack = item.getEntityItem();
                    if (stack != null) {
                        if (fortune > 0) {
                            stack.stackSize += worldObj.rand.nextInt(fortune);
                        }
                        this.addStackToInventory(stack, RelativeSide.TOP);
                    }
                }
                animal.capturedDrops.clear();
                animal.captureDrops = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void pickupEggs() {
        List<EntityItem> items = getEntitiesWithinBounds(EntityItem.class);
        ItemStack stack;
        for (EntityItem item : items) {
            stack = item.getEntityItem();
            if (item.isEntityAlive() && stack != null && stack.getItem() == Items.egg) {
                stack = InventoryTools.mergeItemStack(inventory, stack, inventory.getRawIndices(RelativeSide.TOP));
                if (stack != null) {
                    item.setEntityItemStack(stack);
                }else{
                    item.setDead();
                }
            }
        }
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public void openAltGui(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("maxChickens")) {
            maxChickenCount = tag.getInteger("maxChickens");
        }
        if (tag.hasKey("maxCows")) {
            maxCowCount = tag.getInteger("maxCows");
        }
        if (tag.hasKey("maxPigs")) {
            maxPigCount = tag.getInteger("maxPigs");
        }
        if (tag.hasKey("maxSheep")) {
            maxSheepCount = tag.getInteger("maxSheep");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("maxChickens", maxChickenCount);
        tag.setInteger("maxCows", maxCowCount);
        tag.setInteger("maxPigs", maxPigCount);
        tag.setInteger("maxSheep", maxSheepCount);
    }

    private static class EntityPair {

        final int idA;
        final int idB;

        private EntityPair(Entity a, Entity b) {
            idA = a.getEntityId();
            idB = b.getEntityId();
        }

        public Entity getEntityA(World world) {
            return world.getEntityByID(idA);
        }

        public Entity getEntityB(World world) {
            return world.getEntityByID(idB);
        }
    }

}
