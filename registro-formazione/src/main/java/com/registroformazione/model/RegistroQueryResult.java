package com.registroformazione.model;


import com.registroformazione.utils.Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroQueryResult {

    private String area;
    private String anno;
    private String inForza;
    private String cc;
    private String vendor;
    private String competenza;
    private String cognome;
    private String nome;
    private String nomeCompleto;
    private String tipo;
    private String attivita;
    private String stato;
    private String codice;
    private String dataPianificatoOEseguito;
    private String scadenza;
    private String mesiAScadere;
    private String nota;

    public void setInForza(String input) {
        this.inForza = Util.capitalizeAll(input);
    }

    public void setCc(String input) {
        this.cc = Util.formatString(input);
    }

    public void setVendor(String input) {
        this.vendor = Util.formatString(input);
    }

    public void setCompetenza(String input) {
        this.competenza = Util.formatString(input);
    }

    public void setNome(String input) {
        this.nome = Util.formatString(input);
    }

    public void setCognome(String input) {
        this.cognome = Util.formatString(input);
    }

    public void setTipo(String input) {
        this.tipo = Util.formatString(input);
    }

    public void setStato(String input) {
        this.stato = Util.formatString(input);
    }
    
    public void setArea(String input) {
        this.area = Util.formatString(input);
    }
}

