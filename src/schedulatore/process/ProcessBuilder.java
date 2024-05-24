package schedulatore.process;

public class ProcessBuilder implements ProcessLike {
    private int id;
    private String nome;
    private float durata;
    private float dimensione;
    private Stato stato;
    private int priorita;

    public ProcessBuilder() {
        id = 0;
        nome = "";
        durata = 0;
        dimensione = 0;
        stato = Stato.NUOVO;
        priorita = 0;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getMaxDurata() {
        return durata;
    }

    public void setMaxDurata(float maxDurata) {
        this.durata = maxDurata;
    }

    public float getDimensione() {
        return dimensione;
    }

    public void setDimensione(float dimensione) {
        this.dimensione = dimensione;
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public int getPriorita() {
        return priorita;
    }

    public void setPriorita(int priorita) {
        this.priorita = priorita;
    }

    public Process build() {
        return new Process(id, nome, durata, dimensione, stato, priorita);
    }
}
