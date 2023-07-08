package com.registroformazione.utils;

import java.util.Locale;

public class Util {

	private Util() {
		throw new IllegalStateException("Utility class");
	}

	 public static String formatString(String input) {
	        // Rimuove gli spazi bianchi iniziali e finali
	        String formattedString = input.trim();

	        // Controlla se la stringa è vuota
	        if (formattedString.isEmpty()) {
	            return "";
	        }

	        // Se la stringa contiene un apostrofo, formatta il cognome iniziale
	        if (formattedString.contains("'")) {
	            formattedString = formatSurname(formattedString);
	        } else {
	            // Altrimenti, formatta la stringa come un nome
	            formattedString = formatName(formattedString);
	        }

	        return formattedString;
	    }

	    private static String formatName(String name) {
	        // Converte la prima lettera in maiuscolo e le altre in minuscolo
	        return name.substring(0, 1).toUpperCase(Locale.ITALIAN) + name.substring(1).toLowerCase(Locale.ITALIAN);
	    }

	    private static String formatSurname(String surname) {
	        // Dividere il cognome in base all'apostrofo
	        String[] parts = surname.split("'");
	        StringBuilder formattedSurname = new StringBuilder();

	        // Formatta ogni parte del cognome
	        for (String part : parts) {
	            formattedSurname.append(formatName(part)).append("'");
	        }

	        // Rimuove l'apostrofo finale
	        formattedSurname.setLength(formattedSurname.length() - 1);

	        return formattedSurname.toString();
	    }
	    
	public static String capitalizeFirstLetter(String input) {
	    if (input == null || input.isEmpty()) {
            return input;
        }
        
        char firstChar = Character.toUpperCase(input.charAt(0));
        String restOfString = input.substring(1);
        
        return firstChar + restOfString;
    }
	
	public static String capitalizeAll(String input) {
	    return input.toUpperCase();
	}

}