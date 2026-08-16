/**
 * Represents a command that Henry can recognise.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    UNKNOWN("", false);

    private final String commandWord;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its user-facing command word.
     *
     * @param commandWord word that identifies the command
     * @param acceptsArguments whether text may follow the command word
     */
    CommandType(String commandWord, boolean acceptsArguments) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns the word that identifies this command.
     *
     * @return command word entered by the user
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Finds the command type represented by the given input.
     *
     * @param input complete user input
     * @return matching command type, or {@link #UNKNOWN} when none matches
     */
    public static CommandType from(String input) {
        for (CommandType type : values()) {
            if (type == UNKNOWN) {
                continue;
            }

            boolean isExactMatch = input.equals(type.commandWord);
            boolean hasArguments = type.acceptsArguments
                    && input.startsWith(type.commandWord + " ");
            if (isExactMatch || hasArguments) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
