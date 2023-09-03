public class Inscriere {

    private long cnp;
    private String nume;
    private float nota;
    private int codSpecializare;

    public Inscriere(long cnp, String nume, float nota, int codSpecializare) {
        this.cnp = cnp;
        this.nume = nume;
        this.nota = nota;
        this.codSpecializare = codSpecializare;
    }

    public Inscriere(String linie){
        this.cnp=Long.parseLong(linie.split(",")[0]);
        this.nume=linie.split(",")[1];
        this.nota=Float.parseFloat(linie.split(",")[2]);
        this.codSpecializare=Integer.parseInt(linie.split(",")[3]);
    }

    public Long getCnp() {
        return cnp;
    }

    public void setCnp(Long cnp) {
        this.cnp = cnp;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public int getCodSpecializare() {
        return codSpecializare;
    }

    public void setCodSpecializare(int codSpecializare) {
        this.codSpecializare = codSpecializare;
    }

    @Override
    public String toString() {
        return "Inscriere{" +
                "cnp=" + cnp +
                ", nume='" + nume + '\'' +
                ", nota=" + nota +
                ", codSpecializare=" + codSpecializare +
                '}';
    }
}
