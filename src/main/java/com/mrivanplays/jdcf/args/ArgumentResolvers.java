package com.mrivanplays.jdcf.args;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class containing the default argument resolvers.
 */
public final class ArgumentResolvers {

    public static ArgumentResolver<Integer, Object> INTEGER = new ArgumentResolver<Integer, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                try {
                    Integer.parseInt(resolverContext.getArgument());
                    return ArgumentParsingStates.success();
                } catch (NumberFormatException e) {
                    return ArgumentParsingStates.notType();
                }
            });
        }

        @Override
        public Integer parseNoTests(ArgumentResolverContext context) {
            return Integer.parseInt(context.getArgument());
        }
    };

    public static ArgumentResolver<Double, Object> DOUBLE = new ArgumentResolver<Double, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                try {
                    Double.parseDouble(context.getArgument());
                    return ArgumentParsingStates.success();
                } catch (NumberFormatException e) {
                    return ArgumentParsingStates.notType();
                }
            });
        }

        @Override
        public Double parseNoTests(ArgumentResolverContext context) {
            return Double.parseDouble(context.getArgument());
        }
    };

    public static ArgumentResolver<Float, Object> FLOAT = new ArgumentResolver<Float, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                try {
                    Float.parseFloat(resolverContext.getArgument());
                    return ArgumentParsingStates.success();
                } catch (NumberFormatException e) {
                    return ArgumentParsingStates.notType();
                }
            });
        }

        @Override
        public Float parseNoTests(ArgumentResolverContext context) {
            return null;
        }
    };

    public static ArgumentResolver<User, Object> USER_MENTION = new ArgumentResolver<User, Object>() {

        private final Pattern UM_PATTERN = Pattern.compile("<@!?(\\d{17,20})>");

        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                Matcher matcher = UM_PATTERN.matcher(context.getArgument());
                if (matcher.matches()) {
                    User user = context.getJda().getUserById(matcher.group(1));
                    return user == null ? ArgumentParsingStates.notType() : ArgumentParsingStates.success();
                } else {
                    return ArgumentParsingStates.notType();
                }
            });
        }

        @Override
        public User parseNoTests(@NotNull ArgumentResolverContext context) {
            Matcher matcher = UM_PATTERN.matcher(context.getArgument());
            return context.getJda().getUserById(matcher.group(1));
        }
    };

    public static ArgumentResolver<User, Object> USER_ID = new ArgumentResolver<User, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                try {
                    User user = context.getJda().getUserById(Long.parseLong(context.getArgument()));
                    return user == null ? ArgumentParsingStates.notType() : ArgumentParsingStates.success();
                } catch (NumberFormatException e) {
                    return ArgumentParsingStates.notType();
                }
            });
        }

        @Override
        public User parseNoTests(ArgumentResolverContext context) {
            return null;
        }
    };

    public static ArgumentResolver<User, Object> USER = new ArgumentResolver<User, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            ArgumentParsingState<Object> mention = USER_MENTION.tryParse(resolverContext);
            if (mention.isSuccess()) {
                return mention;
            } else {
                return USER_ID.tryParse(resolverContext);
            }
        }

        @Override
        public User parseNoTests(ArgumentResolverContext context) {
            ArgumentParsingState<Object> mention = USER_MENTION.tryParse(context);
            return mention.isSuccess() ? USER_MENTION.parseNoTests(context) : USER_ID.parseNoTests(context);
        }
    };

    public static ArgumentResolver<Role, Object> ROLE_NAME = new ArgumentResolver<Role, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            if (resolverContext.getArgument().isEmpty()) {
                return ArgumentParsingStates.notPresent();
            }
            if (resolverContext.getGuild() == null) {
                return ArgumentParsingStates.commandNotExecutedInGuild();
            }
            List<Role> roles = resolverContext.getGuild().getRolesByName(resolverContext.getArgument(), true);
            if (roles.isEmpty()) {
                return ArgumentParsingStates.typeNotFound(Role.class);
            }
            return ArgumentParsingStates.success();
        }

        @Override
        public Role parseNoTests(ArgumentResolverContext context) {
            return context.getGuild().getRolesByName(context.getArgument(), true).get(0);
        }
    };

    public static ArgumentResolver<Role, Object> ROLE_ID = new ArgumentResolver<Role, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                if (context.getGuild() == null) {
                    return ArgumentParsingStates.commandNotExecutedInGuild();
                }
                try {
                    Role role = context.getGuild().getRoleById(Long.parseLong(context.getArgument()));
                    if (role == null) {
                        return ArgumentParsingStates.typeNotFound(Role.class);
                    }
                    return ArgumentParsingStates.success();
                } catch (NumberFormatException e) {
                    return ArgumentParsingStates.notType();
                }
            });
        }

        @Override
        public Role parseNoTests(ArgumentResolverContext context) {
            return context.getGuild().getRoleById(Long.parseLong(context.getArgument()));
        }
    };

    public static ArgumentResolver<Role, Object> ROLE = new ArgumentResolver<Role, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                ArgumentParsingState<Object> roleName = ROLE_NAME.tryParse(context);
                if (roleName.isSuccess()) {
                    return roleName;
                } else {
                    return ROLE_ID.tryParse(context);
                }
            });
        }

        @Override
        public Role parseNoTests(ArgumentResolverContext context) {
            ArgumentParsingState<Object> roleName = ROLE_NAME.tryParse(context);
            return roleName.isSuccess() ? ROLE_NAME.parseNoTests(context) : ROLE_ID.parseNoTests(context);
        }
    };

    public static ArgumentResolver<TextChannel, Object> CHANNEL_NAME = new ArgumentResolver<TextChannel, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
                if (context.getGuild() == null) {
                    return ArgumentParsingStates.commandNotExecutedInGuild();
                }
                List<TextChannel> channels = context.getGuild().getTextChannelsByName(context.getArgument(), true);
                if (channels.isEmpty()) {
                    return ArgumentParsingStates.typeNotFound(TextChannel.class);
                }
                return ArgumentParsingStates.success();
            });
        }

        @Override
        public TextChannel parseNoTests(ArgumentResolverContext context) {
            return context.getGuild().getTextChannelsByName(context.getArgument(), true).get(0);
        }
    };

    public static ArgumentResolver<TextChannel, Object> CHANNEL_ID = new ArgumentResolver<TextChannel, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            return checkIfArgumentEmpty(resolverContext, context -> {
               if (context.getGuild() == null) {
                   return ArgumentParsingStates.commandNotExecutedInGuild();
               }
               try {
                   TextChannel channel = context.getGuild().getTextChannelById(Long.parseLong(context.getArgument()));
                   if (channel == null) {
                       return ArgumentParsingStates.typeNotFound(TextChannel.class);
                   }
                   return ArgumentParsingStates.success();
               } catch (NumberFormatException e) {
                   return ArgumentParsingStates.notType();
               }
            });
        }

        @Override
        public TextChannel parseNoTests(ArgumentResolverContext context) {
            return context.getGuild().getTextChannelById(Long.parseLong(context.getArgument()));
        }
    };

    public static ArgumentResolver<TextChannel, Object> CHANNEL = new ArgumentResolver<TextChannel, Object>() {
        @Override
        public ArgumentParsingState<Object> tryParse(ArgumentResolverContext resolverContext) {
            ArgumentParsingState<Object> channelName = CHANNEL_NAME.tryParse(resolverContext);
            if (channelName.isSuccess()) {
                return channelName;
            } else {
                return CHANNEL_ID.tryParse(resolverContext);
            }
        }

        @Override
        public TextChannel parseNoTests(ArgumentResolverContext context) {
            ArgumentParsingState<Object> channelName = CHANNEL_NAME.tryParse(context);
            return channelName.isSuccess() ? CHANNEL_NAME.parseNoTests(context) : CHANNEL_ID.parseNoTests(context);
        }
    };
}
