package schedulatore.process;

public interface ProcessLike {
    int getID();
    String getNome();
    void setNome(String nome);
    float getMaxDurata();
    void setMaxDurata(float maxDurata);
    float getDimensione();
    void setDimensione(float dimensione);
    Stato getStato();
    void setStato(Stato stato);
    int getPriorita();
    void setPriorita(int priorita);
}
