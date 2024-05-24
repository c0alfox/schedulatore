package schedulatore.process;

public enum Stato {
    NUOVO(0),
    PRONTO(1),
    IN_PAUSA(2),
    IN_ESECUZIONE(3),
    IN_CHIUSURA(4);

    private final int value;

    Stato(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
