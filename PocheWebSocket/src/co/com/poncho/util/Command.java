package co.com.poncho.util;

import java.util.HashMap;
import java.util.Map;

public enum Command {
	COMMAND_UNKNOWN, REGISTER_USER(0),VOTE(1),EVAL_RESULTS(2);
	private Integer value = null;
	private static final Map<Integer, Command> intToTypeMap = new HashMap<Integer, Command>();

	private Command() {
	}

	private Command(int commandValue) {
		this.value = commandValue;
	}

	static {
		for (Command type : Command.values()) {
			intToTypeMap.put(type.value, type);
		}
	}

	public static Command fromInt(int i) {
		Command type = intToTypeMap.get(Integer.valueOf(i));
		if (type == null)
			return Command.COMMAND_UNKNOWN;
		return type;
	}

}
