package com.mrivanplays.jdcf.args;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class containing the default argument resolvers.
 */
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

    public static ArgumentResolver<User, Object> USER_MENTION = new ArgumentResolver<User, Object>() {

        private final Pattern UM_PATTERN = Pattern.compile("<@!?(\\d{17,20})>");

        @Override
        public ArgumentResolverResult<User, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                Matcher matcher = UM_PATTERN.matcher(context.getArgument());
                if (matcher.matches()) {
                    User user = context.getJda().getUserById(matcher.group(1));
                    return user == null ?
                            ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition()) :
                            ArgumentResolverResults.success(user);
                } else {
                    return ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<User, Object> USER_ID = new ArgumentResolver<User, Object>() {
        @Override
        public ArgumentResolverResult<User, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                try {
                    User user = context.getJda().getUserById(Long.parseLong(context.getArgument()));
                    return user == null ?
                            ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition()) :
                            ArgumentResolverResults.success(user);
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(User.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<User, Object> USER = context -> {
        ArgumentResolverResult<User, Object> mention = USER_MENTION.parse(context);
        if (mention.getValue().isPresent()) {
            return mention;
        } else {
            return USER_ID.parse(context);
        }
    };

    public static ArgumentResolver<Role, Object> ROLE_NAME = new ArgumentResolver<Role, Object>() {
        @Override
        public ArgumentResolverResult<Role, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                if (proceedContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                List<Role> roles = proceedContext.getGuild().getRolesByName(proceedContext.getArgument(), true);
                if (roles.isEmpty()) {
                    return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition());
                }
                return ArgumentResolverResults.success(roles.get(0));
            });
        }
    };

    public static ArgumentResolver<Role, Object> ROLE_ID = new ArgumentResolver<Role, Object>() {
        @Override
        public ArgumentResolverResult<Role, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
               if (proceedContext.getGuild() == null) {
                   return ArgumentResolverResults.commandNotExecutedInGuild();
               }
               try {
                   Role role = proceedContext.getGuild().getRoleById(Long.parseLong(context.getArgument()));
                   if (role == null) {
                       return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition());
                   }
                   return ArgumentResolverResults.success(role);
               } catch (NumberFormatException e) {
                   return ArgumentResolverResults.valueNotType(Role.class, context.getArgumentPosition());
               }
            });
        }
    };

    public static ArgumentResolver<Role, Object> ROLE = context -> {
        ArgumentResolverResult<Role, Object> roleName = ROLE_NAME.parse(context);
        if (roleName.getValue().isPresent()) {
            return roleName;
        } else {
            return ROLE_ID.parse(context);
        }
    };

    public static ArgumentResolver<TextChannel, Object> CHANNEL_NAME = new ArgumentResolver<TextChannel, Object>() {
        @Override
        public ArgumentResolverResult<TextChannel, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, proceedContext -> {
                if (proceedContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                List<TextChannel> channels = proceedContext.getGuild().getTextChannelsByName(context.getArgument(), true);
                if (channels.isEmpty()) {
                    return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition());
                }
                return ArgumentResolverResults.success(channels.get(0));
            });
        }
    };

    public static ArgumentResolver<TextChannel, Object> CHANNEL_ID = new ArgumentResolver<TextChannel, Object>() {
        @Override
        public ArgumentResolverResult<TextChannel, Object> parse(ArgumentResolveContext context) {
            return checkIfArgumentEmpty(context, resolverContext -> {
                if (resolverContext.getGuild() == null) {
                    return ArgumentResolverResults.commandNotExecutedInGuild();
                }
                try {
                    TextChannel channel = resolverContext.getGuild().getTextChannelById(Long.parseLong(context.getArgument()));
                    if (channel == null) {
                        return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition());
                    }
                    return ArgumentResolverResults.success(channel);
                } catch (NumberFormatException e) {
                    return ArgumentResolverResults.valueNotType(TextChannel.class, context.getArgumentPosition());
                }
            });
        }
    };

    public static ArgumentResolver<TextChannel, Object> CHANNEL = context -> {
        ArgumentResolverResult<TextChannel, Object> channelName = CHANNEL_NAME.parse(context);
        if (channelName.getValue().isPresent()) {
            return channelName;
        } else {
            return CHANNEL_ID.parse(context);
        }
    };
}
