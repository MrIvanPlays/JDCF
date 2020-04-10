package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.args.objects.Mention;
import com.mrivanplays.jdcf.args.objects.ObjectFailData;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * A utility class containing the default argument resolvers.
 */
// todo: more fine checks for object name search
public final class ArgumentResolvers {

    public static ArgumentResolver<Integer, Object> INTEGER = new ArgumentResolver<Integer, Object>() {
        @Override
        public ArgumentResolverResult<Integer, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                try {
                    return ArgumentResolverResults.success(Integer.parseInt(proceedContext.getArgument()));
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(Integer.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<Double, Object> DOUBLE = new ArgumentResolver<Double, Object>() {
        @Override
        public ArgumentResolverResult<Double, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                try {
                    return ArgumentResolverResults.success(Double.parseDouble(proceedContext.getArgument()));
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(Double.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<Float, Object> FLOAT = new ArgumentResolver<Float, Object>() {
        @Override
        public ArgumentResolverResult<Float, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                try {
                    return ArgumentResolverResults.success(Float.parseFloat(proceedContext.getArgument()));
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(Float.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<Long, Object> LONG = new ArgumentResolver<Long, Object>() {
        @Override
        public ArgumentResolverResult<Long, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                try {
                    return ArgumentResolverResults.success(Long.parseLong(context.getArgument()));
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(Long.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<String, Object> STRING = new ArgumentResolver<String, Object>() {
        @Override
        public ArgumentResolverResult<String, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> ArgumentResolverResults.success(proceedContext.getArgument()));
        }
    };

    public static ArgumentResolver<Mention, ObjectFailData> MENTION = new ArgumentResolver<Mention, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<Mention, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                Mention mention = Mention.parse(proceedContext.getArgument(), proceedContext.getJda(), proceedContext.getGuild());
                if (mention == null) {
                    return ArgumentResolverResults.valueNotPresent(ObjectFailData.OBJECT_NOT_FOUND);
                }
                return ArgumentResolverResults.success(mention);
            });
        }
    };

    public static ArgumentResolver<User, ObjectFailData> USER_MENTION = context -> {
        ArgumentResolverResult<Mention, ObjectFailData> mentionResult = MENTION.parse(context);
        Optional<Mention> mentionOptional = mentionResult.getValue();
        if (mentionOptional.isPresent()) {
            Mention mention = mentionOptional.get();
            if (mention.isUserMention()) {
                return mention.toUser() == null ?
                        ArgumentResolverResults.valueNotPresent(ObjectFailData.OBJECT_NOT_FOUND) :
                        ArgumentResolverResults.success(mention.toUser());
            } else {
                return ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED);
            }
        } else {
            return ArgumentResolverResults.valueNotPresent(mentionResult.getFailReason().getAdditionalData());
        }
    };

    public static ArgumentResolver<User, ObjectFailData> USER_NAME = new ArgumentResolver<User, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<User, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                List<User> users = proceedContext.getJda().getUsersByName(proceedContext.getArgument(), true);
                if (users.isEmpty()) {
                    return ArgumentResolverResults.valueNotPresent(ObjectFailData.OBJECT_NOT_FOUND);
                }
                return ArgumentResolverResults.success(users.get(0));
            });
        }
    };

    public static ArgumentResolver<User, ObjectFailData> USER_ID = new ArgumentResolver<User, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<User, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                try {
                    User user = context.getJda().getUserById(Long.parseLong(context.getArgument()));
                    return user == null ?
                            ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED) :
                            ArgumentResolverResults.success(user);
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED);
                }
            });
        }
    };

    public static ArgumentResolver<User, ObjectFailData> USER = context -> {
        ArgumentResolverResult<User, ObjectFailData> name = USER_NAME.parse(context);
        if (name.getValue().isPresent()) {
            return name;
        } else {
            ArgumentResolverResult<User, ObjectFailData> mention = USER_MENTION.parse(context);
            if (mention.getValue().isPresent()) {
                return name;
            } else {
                return USER_ID.parse(context);
            }
        }
    };

    public static ArgumentResolver<Role, ObjectFailData> ROLE_MENTION = context -> {
        ArgumentResolverResult<Mention, ObjectFailData> mentionResult = MENTION.parse(context);
        Optional<Mention> mentionOptional = mentionResult.getValue();
        if (mentionOptional.isPresent()) {
            Mention mention = mentionOptional.get();
            if (mention.isRoleMention()) {
                return mention.toRole() == null ?
                        ArgumentResolverResults.valueNotPresent(ObjectFailData.OBJECT_NOT_FOUND) :
                        ArgumentResolverResults.success(mention.toRole());
            } else {
                return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED);
            }
        } else {
            return ArgumentResolverResults.valueNotPresent(mentionResult.getFailReason().getAdditionalData());
        }
    };

    public static ArgumentResolver<Role, ObjectFailData> ROLE_NAME = new ArgumentResolver<Role, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<Role, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                if (proceedContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                List<Role> roles = proceedContext.getGuild().getRolesByName(proceedContext.getArgument(), true);
                if (roles.isEmpty()) {
                    return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_FOUND);
                }
                return ArgumentResolverResults.success(roles.get(0));
            });
        }
    };

    public static ArgumentResolver<Role, ObjectFailData> ROLE_ID = new ArgumentResolver<Role, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<Role, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                if (proceedContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                try {
                    Role role = proceedContext.getGuild().getRoleById(Long.parseLong(context.getArgument()));
                    if (role == null) {
                        return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_FOUND);
                    }
                    return ArgumentResolverResults.success(role);
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED);
                }
            });
        }
    };

    public static ArgumentResolver<Role, ObjectFailData> ROLE = context -> {
        ArgumentResolverResult<Role, ObjectFailData> roleName = ROLE_NAME.parse(context);
        if (roleName.getValue().isPresent()) {
            return roleName;
        } else {
            ArgumentResolverResult<Role, ObjectFailData> roleMention = ROLE_MENTION.parse(context);
            if (roleMention.getValue().isPresent()) {
                return roleMention;
            } else {
                return ROLE_ID.parse(context);
            }
        }
    };

    public static ArgumentResolver<TextChannel, ObjectFailData> CHANNEL_MENTION = context -> {
        ArgumentResolverResult<Mention, ObjectFailData> mentionResult = MENTION.parse(context);
        Optional<Mention> mentionOptional = mentionResult.getValue();
        if (mentionOptional.isPresent()) {
            Mention mention = mentionOptional.get();
            if (mention.isChannelMention()) {
                return mention.toChannel() == null ?
                        ArgumentResolverResults.valueNotPresent(ObjectFailData.OBJECT_NOT_FOUND) :
                        ArgumentResolverResults.success(mention.toChannel());
            } else {
                return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED);
            }
        } else {
            return ArgumentResolverResults.valueNotPresent(mentionResult.getFailReason().getAdditionalData());
        }
    };

    public static ArgumentResolver<TextChannel, ObjectFailData> CHANNEL_NAME = new ArgumentResolver<TextChannel, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<TextChannel, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                if (proceedContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                List<TextChannel> channels = proceedContext.getGuild().getTextChannelsByName(context.getArgument(), true);
                if (channels.isEmpty()) {
                    return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_FOUND);
                }
                return ArgumentResolverResults.success(channels.get(0));
            });
        }
    };

    public static ArgumentResolver<TextChannel, ObjectFailData> CHANNEL_ID = new ArgumentResolver<TextChannel, ObjectFailData>() {
        @Override
        public ArgumentResolverResult<TextChannel, ObjectFailData> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, resolverContext -> {
                if (resolverContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                try {
                    TextChannel channel = resolverContext.getGuild().getTextChannelById(Long.parseLong(context.getArgument()));
                    if (channel == null) {
                        return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_FOUND);
                    }
                    return ArgumentResolverResults.success(channel);
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition(), ObjectFailData.OBJECT_NOT_TYPE_ASKED);
                }
            });
        }
    };

    public static ArgumentResolver<TextChannel, ObjectFailData> CHANNEL = context -> {
        ArgumentResolverResult<TextChannel, ObjectFailData> channelName = CHANNEL_NAME.parse(context);
        if (channelName.getValue().isPresent()) {
            return channelName;
        } else {
            ArgumentResolverResult<TextChannel, ObjectFailData> channelMention = CHANNEL_MENTION.parse(context);
            if (channelMention.getValue().isPresent()) {
                return channelMention;
            } else {
                return CHANNEL_ID.parse(context);
            }
        }
    };
}
