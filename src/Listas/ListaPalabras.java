package Listas;

import sample.Ficha;

public class ListaPalabras {
    int largo;
    Nodo<String> head;

    public ListaPalabras() {
        largo=0;
        head=null;
    }

    public void addFirst(String e) {
        if (e==""){}
        else {
            Nodo<String> n = new Nodo<String>(e);
            System.out.println("se anadio "+n.getNodo());
            n.next = this.head;
            this.head = n;
            largo += 1;
        }
    }
    public void eliminar(String palabra){
        if (this.head.getNodo().equals(palabra)){
            this.head=this.head.next;
            largo-=1;
        }
        else{
            Nodo<String>tmp=this.head;
            while (tmp.next!=null){
                if (tmp.next.getNodo().equals(palabra)){
                    tmp.next=tmp.next.next;
                    largo-=1;
                    break;
                }
                else {
                    tmp=tmp.next;
                }
            }
        }
    }
    public String buscar (int n){
        Nodo<String>tmp=this.head;
        while (n>0){
            tmp=tmp.next;
            n--;
        }
        return tmp.getNodo();
    }
    

    public int getLargo() {
        return largo;
    }

    public void setLargo(int largo) {
        this.largo = largo;
    }

    public Nodo <String> getHead() {
        return head;
    }

    public void setHead(Nodo <String> head) {
        this.head = head;
    }
    public ListaFichas convertirfichas(){
        int n=0;
        ListaFichas listaFichas= new ListaFichas();
        if (this.head!=null) {
            System.out.println("voy a convertir a ficha");
            while (n < this.largo) {
                listaFichas.addFirst(new Ficha(0, 0, this.buscar(n)));
                System.out.println(listaFichas.buscar(0).getLetra());
                n++;
            }
        }
        return listaFichas;
    }
    public int sacarpuntaje(){
        int puntaje=0,n=0;
        String palabra;
        Ficha ficha;
        while (n<this.getLargo()){
            palabra= this.buscar(n);
            int i=0;
            while (i<palabra.length()) {
                ficha = new Ficha(0, 0, palabra.substring(i, i + 1));
                puntaje += ficha.getValor();
                i++;
            }
            n++;
        }
        return puntaje;
    }
    public String concatenarpalabras(){
        int cont=1;
        String lista=this.buscar(0);
        while (cont<this.largo){
            lista+=","+this.buscar(cont);
            cont++;
        }
        return lista;
    }
}

