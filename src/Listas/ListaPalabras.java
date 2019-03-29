package Listas;

public class ListaPalabras {
    int largo;
    Nodo<String> head= null;


    public void addLast (String e){
        if (this.head.getNodo()==null){
            this.head= new Nodo(e);
        }
        else {
            Nodo tmp= this.head;
            while (tmp.next!= null) {
                tmp = tmp.next;
            }
            tmp.next=new Nodo(e);
        }
    }
    public void addFirst(String e) {
        Nodo n = new Nodo(e);
        n.next=this.head;
        head=n;
    }

    public int getLargo() {
        return largo;
    }

    public void setLargo(int largo) {
        this.largo = largo;
    }

    public Nodo getHead() {
        return head;
    }

    public void setHead(Nodo head) {
        this.head = head;
    }
}
