package schedulatore.process;

public class Process extends Thread implements ProcessLike {
    private final int id;
    private String nome;
    private float maxDurata;   // (s)
    private float durata;
    private float dimensione; // (KB)
    private volatile Stato stato;
    private int priorita;

    public Process(int id, String nome, float maxDurata, float dimensione, Stato stato, int priorita) {
        this.id = id;
        this.nome = nome;
        this.maxDurata = maxDurata;
        this.dimensione = dimensione;
        this.stato = stato;
        setPriorita(priorita);
    }

    public int getID() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getMaxDurata() {
        return maxDurata;
    }

    public void setMaxDurata(float maxDurata) {
        this.maxDurata = maxDurata;
        durata = Math.min(durata, maxDurata);
    }

    public float getDurata() {
        return durata;
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
        this.priorita = Math.min(Math.max(-20, priorita), 20);
    }

    @Override
    public String toString() {
        return String.format("PID: %d, %s | Dimensione: %.2f KiB | Priorità: %d | (%.2f sec) %s",
                id, nome, dimensione, priorita, durata, stato);
    }

    @Override
    public synchronized void run() {
        super.run();

        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        while (true) {
            switch (stato) {
                case NUOVO:
                case IN_ESECUZIONE:
                case PRONTO:
                case IN_PAUSA:
                    execute();
                    break;
                case IN_CHIUSURA:
                    kill();
                    return;
            }

            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    private synchronized void execute() {

        while (maxDurata > durata) {
            if (stato == Stato.IN_PAUSA) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (stato == Stato.IN_CHIUSURA) {
                kill();
                return;
            }

            float sleep_seconds = Math.min(0.5f, maxDurata - durata);
            int sleep_millis = (int)(sleep_seconds * 1000);
            try {
                wait(sleep_millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            this.durata += sleep_seconds;

            refresh();
        }

        setRuntimeState(Stato.IN_CHIUSURA);
        kill();
    }

    public void kill() {
        super.run();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        ProcessManager.getInstance().remove(this);
    }

    private synchronized void setRuntimeState(Stato s) {
        Stato prev = stato;
        stato = s;
        ProcessManager.getInstance().setProcess(this);

        switch (s) {
            case NUOVO:
                break;

            case PRONTO:
            case IN_PAUSA:
                ProcessManager.getInstance().onProcessStop();
                break;

            case IN_CHIUSURA:
                if (prev == Stato.IN_ESECUZIONE) {
                    ProcessManager.getInstance().onProcessStop();
                }
                break;

            case IN_ESECUZIONE:
                ProcessManager.getInstance().onProcessStart();
                break;
        }
    }

    public synchronized void signal(Interrupt i) {
        if (stato == Stato.IN_CHIUSURA) {
            return;
        }

        switch (i) {
            case START:
                setRuntimeState(Stato.IN_ESECUZIONE);
                break;
            case PAUSE:
                setRuntimeState(stato == Stato.IN_PAUSA ? Stato.IN_ESECUZIONE : Stato.IN_PAUSA);
                break;
            case KILL:
                setRuntimeState(Stato.IN_CHIUSURA);
                break;
        }

        notify();
    }

    private void refresh() {
        ProcessManager.getInstance().setProcess(this);
    }

    public void edit(ProcessLike oth) {
        nome = oth.getNome();
        maxDurata = oth.getMaxDurata();
        dimensione = oth.getDimensione();
        priorita = oth.getPriorita();
    }

    private int getRandomTime() {
        return (int)(Math.random() * (10000 - 1000 + 1) + 1000);
    }
}
