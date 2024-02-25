package dev.wuffs.squatgrow.config;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public record ComputedRequirements(
        List<ItemStack> heldItemRequirementStacks,
        List<TagKey<Item>> heldItemRequirementTags,
        Map<EquipmentSlot, ItemStack> equipmentRequirementStacks,
        Map<EquipmentSlot, TagKey<Item>> equipmentRequirementTags
) {}
