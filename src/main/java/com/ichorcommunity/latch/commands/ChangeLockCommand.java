/*
 * This file is part of Latch, licensed under the MIT License (MIT).
 *
 * Copyright (c) Ichor Community <http://www.ichorcommunity.com>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ichorcommunity.latch.commands;

import com.ichorcommunity.latch.Latch;
import com.ichorcommunity.latch.enums.LockType;
import com.ichorcommunity.latch.interactions.ChangeLockInteraction;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

public class ChangeLockCommand implements CommandExecutor {

    private CommandFlags.Builder flagBuilder = GenericArguments.flags();

    public CommandCallable getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Change the attributes of a lock"))
                .permission("latch.normal.change")
                .executor(this)
                .arguments(GenericArguments.optionalWeak(
                        flagBuilder
                                .valueFlag(GenericArguments.string(Text.of("name")), "-name")
                                .valueFlag(GenericArguments.enumValue(Text.of("type"), LockType.class), "-type")
                                .valueFlag(GenericArguments.user(Text.of("owner")), "-owner")
                                .valueFlag(GenericArguments.string(Text.of("password")), "-password")
                                .valueFlag(GenericArguments.user(Text.of("add")), "-add")
                                .valueFlag(GenericArguments.user(Text.of("remove")), "-remove")
                                .permissionFlag("latch.normal.persist", "persist", "p")
                                .buildWith(GenericArguments.none())))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player) {

            if( !(args.hasAny("name") || args.hasAny("type") || args.hasAny("owner") || args.hasAny("password") || args.hasAny("add") || args.hasAny("remove")) ) {
                ((Player) src).sendMessage(Text.of("You must specify at least one attribute to change."));
                return CommandResult.empty();
            }

            ChangeLockInteraction changeLock = new ChangeLockInteraction(((Player) src).getUniqueId());

            Optional<String> optionalString = args.<String>getOne("name");
            if(optionalString.isPresent()) {
                if(optionalString.get().length() <= 25) {
                    changeLock.setLockName(optionalString.get());
                } else {
                    ((Player) src).sendMessage(Text.of("Lock names must be less than 25 characters long."));
                    return CommandResult.empty();
                }
            }

            Optional<LockType> optionalType = args.<LockType>getOne("type");
            if(optionalType.isPresent()) {
                changeLock.setType(optionalType.get());
            }

            Optional<User> optionalUser = args.<User>getOne("owner");
            if(optionalUser.isPresent()) {
                changeLock.setNewOwner(optionalUser.get().getUniqueId());
            }

            optionalString = args.<String>getOne("password");
            if(optionalString.isPresent()) {
                changeLock.setPassword(optionalString.get());
            }

            Collection<User> members = args.<User>getAll("add");
            if(members.size() > 0) {
                changeLock.setMembersToAdd(members);
            }

             members = args.<User>getAll("remove");
            if(members.size() > 0) {
                changeLock.setMembersToRemove(members);
            }

            changeLock.setPersistance(args.hasAny("p"));

            Latch.getLockManager().setInteractionData(((Player) src).getUniqueId(), changeLock);

            ((Player) src).sendMessage(Text.of("You will change the next lock you click."));

            return CommandResult.success();
        }

        return CommandResult.empty();
    }

}
