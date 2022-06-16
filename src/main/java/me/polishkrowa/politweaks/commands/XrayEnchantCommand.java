package me.polishkrowa.politweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.timer.Timer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class XrayEnchantCommand {
    private static Map<UUID, Direction> lastClicked = new HashMap();
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("xrayenchant").executes((context) -> {
            return execute(context.getSource());
        }));


        AttackBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) -> {
            lastClicked.put(player.getUuid(), direction);
//            System.out.println("1");
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.AFTER.register((World world, PlayerEntity playerEnt, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) -> {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerEnt.getUuid());

            if (hasEnchant(player.getInventory().getMainHandStack(), player) && lastClicked.containsKey(player.getUuid())) {
//                System.out.println("2");
                //damage manager
//                if (player.interactionManager.getGameMode() == GameMode.SURVIVAL) {
//                    ItemStack damageable = player.getInventory().getMainHandStack();
//                    int maxDurability = player.getInventory().getMainHandStack().getItem().getMaxDamage();
//                    damageable.setDamage((int)((double)damageable.getDamage() + (double)maxDurability * 0.03D));
//                    if (damageable.getDamage() >= maxDurability) {
//                        //break item
//                        player.getInventory().getMainHandStack().setCount(0);
//                        player.getWorld().playSound(player, player.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
//                    }
//
//                }

                //packet sender
                Direction blockFace = lastClicked.get(player.getUuid());
                int count = 70;
                int level = getEnchantLevel(player.getInventory().getMainHandStack().getTooltip(player, TooltipContext.Default.NORMAL));

                for(BlockPos blockPos = pos; world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && count > 0; blockPos = blockPos.offset(blockFace.getOpposite())) {
                    for(int x = -(level / 2); x <= level / 2; ++x) {
                        for(int z = -(level / 2); z <= level / 2; ++z) {
                            if (level % 2 != 0 || Math.abs(x) != level / 2 || Math.abs(z) != level / 2) {
                                BlockPos newBlock = null;
                                if (blockFace != Direction.DOWN && blockFace != Direction.UP) {
                                    if (blockFace != Direction.EAST && blockFace != Direction.WEST) {
                                        if (blockFace == Direction.NORTH || blockFace == Direction.SOUTH) {
                                            newBlock = blockPos.add(x,z,0);
                                        }
                                    } else {
                                        newBlock = blockPos.add(0, x, z);
                                    }
                                } else {
                                    newBlock = blockPos.add(x, 0, z);
                                }

//                                System.out.println(newBlock.toShortString());

                                if (world.getBlockState(newBlock) != null) {
                                    Block type = world.getBlockState(newBlock).getBlock();
                                    if (!type.getName().getString().endsWith("ORE") && type != Blocks.AIR && type != Blocks.LAVA && type != Blocks.WATER && type != Blocks.CHEST && type != Blocks.SPAWNER && type != Blocks.END_PORTAL_FRAME && type != Blocks.BEDROCK && !type.getName().getString().contains("PLANKS") && !type.getName().getString().contains("FRENCE") && type != Blocks.RAIL && type != Blocks.ANCIENT_DEBRIS && type != Blocks.AMETHYST_BLOCK && type != Blocks.BUDDING_AMETHYST && !type.getName().getString().contains("AMETHYST")) {
                                        for (ServerPlayerEntity playere : world.getServer().getPlayerManager().getPlayerList()) {
                                            playere.networkHandler.sendPacket(new BlockUpdateS2CPacket(newBlock, Blocks.BARRIER.getDefaultState()));
                                        }
                                    } else {
//                                        for (ServerPlayerEntity playere : world.getServer().getPlayerManager().getPlayerList()) {
//                                            playere.networkHandler.sendPacket(new BlockUpdateS2CPacket(newBlock, type.getDefaultState()));
//                                        }
                                    }

                                }
                            }
                        }
                    }

                    --count;
                }


                Direction lastface = (Direction) lastClicked.get(player.getUuid());
                int levelEnch = getEnchantLevel(player.getInventory().getMainHandStack().getTooltip(player, TooltipContext.Default.NORMAL));

                long l = player.getWorld().getTime() + (long)200;
                Timer<MinecraftServer> timer = player.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
                timer.setEvent("string", l, (server, events, time1) -> {
//                    System.out.println("executed task");

                    Direction blockFacee = lastface;
                    int countt = 70;
                    int levell = levelEnch;

                    for(BlockPos blockPos = pos; world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && countt > 0; blockPos = blockPos.offset(blockFacee.getOpposite())) {
                        for(int x = -(levell / 2); x <= levell / 2; ++x) {
                            for(int z = -(levell / 2); z <= levell / 2; ++z) {
                                if (levell % 2 != 0 || Math.abs(x) != levell / 2 || Math.abs(z) != levell / 2) {
                                    BlockPos newBlock = null;
                                    if (blockFacee != Direction.DOWN && blockFacee != Direction.UP) {
                                        if (blockFacee != Direction.EAST && blockFacee != Direction.WEST) {
                                            if (blockFacee == Direction.NORTH || blockFacee == Direction.SOUTH) {
                                                newBlock = blockPos.add(x,z,0);
                                            }
                                        } else {
                                            newBlock = blockPos.add(0, x, z);
                                        }
                                    } else {
                                        newBlock = blockPos.add(x, 0, z);
                                    }


                                    if (world.getBlockState(newBlock) != null) {

                                        for (ServerPlayerEntity playere : world.getServer().getPlayerManager().getPlayerList()) {
                                            playere.networkHandler.sendPacket(new BlockUpdateS2CPacket(newBlock, world.getBlockState(newBlock)));
                                        }

                                    }
                                }
                            }
                        }

                        --countt;
                    }
                });

            }
        });
    }



    public static int execute(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        int level = 5;

        ItemStack item = player.getInventory().getMainHandStack();
        if (!isTool(item.getItem())) {
            source.sendError(Text.literal("You must be holding a tool!"));
        } else {
            item.addEnchantment(Enchantments.BLAST_PROTECTION, 1);

            item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);


            if (hasEnchant(item)) {
                source.sendError(Text.literal("Item already has Xray enchantment."));
                return 0;
            } else {
//                item.getTooltip(player, TooltipContext.Default.NORMAL).add(Text.literal(toLore(level).formatted(Formatting.GRAY)));


//                NbtCompound display = item.getOrCreateNbt().getCompound("display");
//                display.put("Lore", list);
//                item.setNbt(item.getOrCreateNbt().);

                NbtCompound nbtCompound = item.getOrCreateSubNbt("display");
                NbtList list = nbtCompound.getList("Lore", 8);
                list.add(NbtString.of(Text.Serializer.toJson(Text.literal("Xray " + toLore(level)).formatted(Formatting.RESET,Formatting.GRAY))));
                nbtCompound.put("Lore", list);



                String lore = "Successfully added Xray " + toLore(level) + ".";
                source.sendFeedback(Text.literal(lore).formatted(Formatting.GREEN), false);
            }
        }

        return 0;
    }

    //TODO: NOT WORKING WHYYYYYY
    private static boolean hasEnchant(ItemStack itemStack) {
        NbtCompound nbtCompound = itemStack.getOrCreateSubNbt("display");
//        System.out.println(nbtCompound.asString());
        NbtList list = nbtCompound.getList("Lore", 8);
//        System.out.println(list.asString());

        for (NbtElement nbtElement : list) {
//            System.out.println(nbtElement.asString());
            if (nbtElement.asString().equalsIgnoreCase(Text.Serializer.toJson(Text.literal("Xray " + toLore(5)).formatted(Formatting.GRAY))))
                return true;
        }

        return false;
//        return getEnchantLevel(itemStack.getTooltip(player, TooltipContext.Default.NORMAL)) != -1;
    }

    private static boolean hasEnchant(ItemStack itemStack, PlayerEntity player) {
        return hasEnchant(itemStack);
//        return getEnchantLevel(itemStack.getTooltip(player, TooltipContext.Default.NORMAL)) != -1;
    }

    private static int getEnchantLevel(List<Text> lore) {
        return 5;
//        for (Text text : lore) {
//            System.out.println(text.getString());
//            if (lore.get(0).equals(text)) return -1;
//            if (text.getString().contains("Xray")) {
//                String split = text.copyContentOnly().toString().split("Xray ")[1].trim();
//                return fromRoman(split);
//            }
//        }
//        return -1;
    }

    private static boolean isTool(Item material) {
        String name = material.getName().getString();
//        System.out.println(name);
        return name.contains("Pickaxe") || name.contains("Axe") || name.contains("Shovel");
    }


    private static String toLore(int level) {
        switch(level) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "enchantment.level." + level;
        }
    }
}
