package it.uniroma3.siw.model;

public enum OrderStatus {
	IN_CREAZIONE,    // ordine ancora da completare (come un carrello)
    EFFETTUATO,         // o CONSEGNATO, ecc.
    ANNULLATO
}
