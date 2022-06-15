package me.polishkrowa.politweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.sound.Sound;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.*;

public class XrayEnchantCommand {
    private static Map<UUID, Direction> lastClicked = new HashMap();
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("xrayenchant").executes((context) -> {
            return execute(context.getSource());
        }));


        AttackBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) -> {
            lastClicked.put(player.getUuid(), direction);
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.AFTER.register((World world, ServerPlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) -> {

            if (hasEnchant(player.getInventory().getMainHandStack(), player) && lastClicked.containsKey(player.getUuid())) {

                //damage manager
                if (player.interactionManager.getGameMode() == GameMode.SURVIVAL) {
                    ItemStack damageable = player.getInventory().getMainHandStack();
                    int maxDurability = player.getInventory().getMainHandStack().getItem().getMaxDamage();
                    damageable.setDamage((int)((double)damageable.getDamage() + (double)maxDurability * 0.03D));
                    if (damageable.getDamage() >= maxDurability) {
                        //break item
                        player.getInventory().getMainHandStack().setCount(0);
                        player.getWorld().playSound(player, player.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }

                //packet sender
                Direction blockFace = lastClicked.get(player.getUuid());
                int count = 70;
                int level = getEnchantLevel(player.getInventory().getMainHandStack().getTooltip(player, TooltipContext.Default.NORMAL));

                for(BlockPos blockPos = pos; world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && count > 0; blockPos = pos.offset(blockFace)) {
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


                                if (world.getBlockState(newBlock) != null) {
                                    Block type = world.getBlockState(newBlock).getBlock();
                                    if (!type.getName().getString().endsWith("ORE") && type != Blocks.AIR && type != Blocks.LAVA && type != Blocks.WATER && type != Blocks.CHEST && type != Blocks.SPAWNER && type != Blocks.END_PORTAL_FRAME && type != Blocks.BEDROCK && !type.getName().getString().contains("PLANKS") && !type.getName().getString().contains("FRENCE") && type != Blocks.RAIL && type != Blocks.ANCIENT_DEBRIS && type != Blocks.AMETHYST_BLOCK && type != Blocks.BUDDING_AMETHYST && !type.getName().getString().contains("AMETHYST")) {
                                        for (ServerPlayerEntity playere : world.getServer().getPlayerManager().getPlayerList()) {
                                            playere.networkHandler.sendPacket(new BlockUpdateS2CPacket(newBlock, Blocks.BARRIER.getDefaultState()));
                                        }
                                    } else {
                                        for (ServerPlayerEntity playere : world.getServer().getPlayerManager().getPlayerList()) {
                                            playere.networkHandler.sendPacket(new BlockUpdateS2CPacket(newBlock, type.getDefaultState()));
                                        }
                                    }

                                }
                            }
                        }
                    }

                    --count;
                }

                //TODO update this
                BlockFace lastface = (BlockFace)lastClicked.get(player.getUniqueId());
                int levelEnch = getEnchantLevel(player.getInventory().getItemInMainHand());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BlockFace blockFace = lastface;
                        int count = 70;
                        int level = levelEnch;

                        for(Block block = event.getBlock(); block.getType() != Material.BEDROCK && count > 0; block = block.getRelative(blockFace)) {
                            for(int x = -(level / 2); x <= level / 2; ++x) {
                                for(int z = -(level / 2); z <= level / 2; ++z) {
                                    if (level % 2 != 0 || Math.abs(x) != level / 2 || Math.abs(z) != level / 2) {
                                        Block newBlock = null;
                                        if (blockFace != BlockFace.DOWN && blockFace != BlockFace.UP) {
                                            if (blockFace != BlockFace.EAST && blockFace != BlockFace.WEST) {
                                                if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
                                                    newBlock = block.getRelative(x, z, 0);
                                                }
                                            } else {
                                                newBlock = block.getRelative(0, x, z);
                                            }
                                        } else {
                                            newBlock = block.getRelative(x, 0, z);
                                        }

                                        if (newBlock != null) {
                                            //Material type = newBlock.getType();
                                            Iterator var11 = Bukkit.getOnlinePlayers().iterator();

                                            while(var11.hasNext()) {
                                                Player onlinePlayer = (Player)var11.next();
                                                onlinePlayer.sendBlockChange(newBlock.getLocation(), newBlock.getBlockData());
                                            }
                                        }
                                    }
                                }
                            }

                            --count;
                        }

                    }
                }.runTaskLater(this, 200);
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

            getEnchantLevel(item.getTooltip(player, TooltipContext.Default.NORMAL));

            if (getEnchantLevel(item.getTooltip(player, TooltipContext.Default.NORMAL)) == -1) {
                source.sendError(Text.literal("Item already has Xray enchantment."));
                return 0;
            } else {
                item.getTooltip(player, TooltipContext.Default.NORMAL).add(Text.literal(toLore(level).formatted(Formatting.GRAY)));
                String lore = "Successfully added Xray " + (level > 10 ? level : toLore(level)) + ".";
                source.sendFeedback(Text.literal(lore).formatted(Formatting.GREEN), false);
            }
        }

        return 0;
    }


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {


    }

    private static boolean hasEnchant(ItemStack itemStack, PlayerEntity player) {
        return getEnchantLevel(itemStack.getTooltip(player, TooltipContext.Default.NORMAL)) != -1;
    }

    private static int getEnchantLevel(List<Text> lore) {
        for (Text text : lore) {
            if (lore.get(0).equals(text)) return -1;
            if (text.getString().contains("Xray")) {
                String split = text.copyContentOnly().toString().split("Xray ")[1].trim();
                return fromRoman(split);
            }
        }
        return -1;
    }

    private static boolean isTool(Item material) {
        String name = material.toString();
        return name.contains("_PICKAXE") || name.contains("_AXE") || name.contains("_SHOVEL");
    }

    public static int fromRoman(String roman) {
        byte var3 = -1;
        switch(roman.hashCode()) {
            case 73:
                if (roman.equals("I")) {
                    var3 = 0;
                }
                break;
            case 86:
                if (roman.equals("V")) {
                    var3 = 4;
                }
                break;
            case 88:
                if (roman.equals("X")) {
                    var3 = 9;
                }
                break;
            case 2336:
                if (roman.equals("II")) {
                    var3 = 1;
                }
                break;
            case 2349:
                if (roman.equals("IV")) {
                    var3 = 3;
                }
                break;
            case 2351:
                if (roman.equals("IX")) {
                    var3 = 8;
                }
                break;
            case 2739:
                if (roman.equals("VI")) {
                    var3 = 5;
                }
                break;
            case 72489:
                if (roman.equals("III")) {
                    var3 = 2;
                }
                break;
            case 84982:
                if (roman.equals("VII")) {
                    var3 = 6;
                }
                break;
            case 2634515:
                if (roman.equals("VIII")) {
                    var3 = 7;
                }
        }

        switch(var3) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            case 8:
                return 9;
            case 9:
                return 10;
            default:
                return -1;
        }
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
