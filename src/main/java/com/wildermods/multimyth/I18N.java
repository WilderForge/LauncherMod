package com.wildermods.multimyth;

public class I18N {

	public static String translate(String text, String... params) {
		return Launcher.dependencies.gameStrings.ui(text, params);
	}
	
	public static String translate(String text, Object... params) {
		String[] toStringParams = new String[params.length];
		for(int i = 0; i < params.length; i++) {
			toStringParams[i] = params[i].toString();
		}
		return Launcher.dependencies.gameStrings.ui(text, toStringParams);
	}
	
}
