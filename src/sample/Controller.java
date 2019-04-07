package sample;


import Listas.ListaFichas;
import Listas.ListaPalabras;
import Listas.Matriz;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.*;

public class Controller {
    @FXML public  Pane juegopane= new Pane();
    public static Logger log = LoggerFactory.getLogger(Controller.class);
    private Double orgSceneX;
    private Double orgSceneY;
    public  Matriz matriz= new Matriz();
    public TextField nombrefield;
    public Datos datos= new Datos();
    @FXML private Label labelturno=new Label();
    ObjectMapper objectMapper=new ObjectMapper();
    public TextField comprobacionfield= new TextField();
    public ListaFichas listaFichas=new ListaFichas();


//    Menu de Inicio
    public TextField codigofield,nombref= new TextField();
    public ComboBox<Integer> jugadoresbox= new ComboBox<Integer>();
    public Pane menupane= new Pane();



    public void espera() {
        datos.setClient(nombrefield.getText());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Turno(datos));
        log.debug("aqui llega");
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                try {
                    if (future.isDone()) {
                    String resp= future.get();
                    log.debug("future terminó");
                    if (resp.contains("Tu_turno")){
                        log.debug("turno del cliente");
                        log.debug("se va a cambiar label");
                        labelturno.setText(resp);
                    }
                    else{
                        log.debug("se llego a actualizar");
                        datos.setAccion("Actualizar");
                        Socket client = new Socket(InetAddress.getLocalHost(), 9500);
                        log.debug("se conecto");
                        DataOutputStream datosenvio= new DataOutputStream(client.getOutputStream());
                        datosenvio.writeUTF(objectMapper.writeValueAsString(datos));
                        log.debug("se envio objeto");
                        DataInputStream datosentrada= new DataInputStream(client.getInputStream());
                        log.debug("entrada se conecto");
                        Datos datosrecibidos=objectMapper.readValue(datosentrada.readUTF(), Datos.class);
                        log.debug("se creo objeto");
                        matriz.agregar(datosrecibidos.getMatriz(),juegopane);
                        labelturno.setText("si lo logré");
                        datosenvio.close();
                        client.close();
                    }
                    stop();
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }




    public void pasar_turno(){

        try {
            datos.setAccion("Pasar");
            Socket client = new Socket(InetAddress.getLocalHost(), 9500);
            log.debug("se conecto");
            DataOutputStream datosenvio= new DataOutputStream(client.getOutputStream());
            datosenvio.writeUTF(objectMapper.writeValueAsString(this.datos));
            log.debug("se envio objeto");
            this.espera();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void iniciar(){
        if (nombref.getText().equals("")){
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Espacio en blanco");
            alert.setContentText("debe escrbir un nombre de jugador para iniciar una partida");
            alert.showAndWait();
        }
        else{
            try {
                this.datos.setClient(nombref.getText());
                this.datos.setJugadores(jugadoresbox.getValue());
                Socket client = new Socket(InetAddress.getLocalHost(), 9500);
                log.debug("iniciar");
                DataOutputStream datosenvio= new DataOutputStream(client.getOutputStream());
                datosenvio.writeUTF(objectMapper.writeValueAsString(this.datos));
                DataInputStream datosentrada= new DataInputStream(client.getInputStream());
                log.debug("entrada se conecto");
                Datos datosrecibidos=objectMapper.readValue(datosentrada.readUTF(), Datos.class);
                log.debug("se creo objeto");
                if (datosrecibidos.getRespueta().equals("server_usado")){
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Server usado");
                    alert.setContentText("el server esta siendo usado");
                    alert.showAndWait();
                    datosenvio.close();
                    client.close();
                }
                else{
                    Alert alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Codigo");
                    alert.setContentText("El codigo de entrada es"+datosrecibidos.getCodigo());
                    alert.showAndWait();
                    datosenvio.close();
                    client.close();
                }

            } catch (IOException e) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error inesperado");
                alert.setContentText("ocurrió un error inesperado");
                alert.showAndWait();
            }

        }
    }

    public void unirse (){
        try {
            if (nombrefield.getText().equals("") || codigofield.getText().equals("")){
                Alert alert=new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Espacio en blanco");
                alert.setContentText("debe escrbir el nombre de jugador y el codigo para unirse a una partida");
                alert.showAndWait();
            }
            else {
                datos.setClient(nombrefield.getText());
                datos.setCodigo(Integer.parseInt(codigofield.getText()));
                datos.setAccion("unirse");
                Socket client = new Socket(InetAddress.getLocalHost(), 9500);
                log.debug("unirse");
                DataOutputStream datosenvio = new DataOutputStream(client.getOutputStream());
                datosenvio.writeUTF(objectMapper.writeValueAsString(this.datos));
                DataInputStream datosentrada = new DataInputStream(client.getInputStream());
                log.debug("entrada se conecto");
                Datos datosrecibidos = objectMapper.readValue(datosentrada.readUTF(), Datos.class);
                log.debug("se creo objeto");
                if (datosrecibidos.getRespueta().equals("Partida_llena")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Server usado");
                    alert.setContentText("Partida llena");
                    alert.showAndWait();
                } else if (datosrecibidos.getRespueta().equals("Codigo_Erroneo")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Codigo Erroneo");
                    alert.setContentText("el codigo es incorrecto");
                    alert.showAndWait();
                } else if (datosrecibidos.getRespueta().equals("No hay partida")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Server sin uso");
                    alert.setContentText("No existe partida creada: inicie una o espere");
                    alert.showAndWait();
                } else{}
            }
        }
        catch (NumberFormatException e){
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Codigo Erroneo");
            alert.setContentText("el codigo esta compuesto de numeros");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void comprobar(){
        try {
            if (listaFichas.getLargo()==7){
                Alert alert=new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setContentText("no se ha realizado ninguna jugada");
                alert.showAndWait();
            }
            else {
                datos.setAccion("comprobar");
                datos.setMatriz(matriz.convertir());
                datos.setListacliente(datos.getListacliente());
                Socket client = null;
                client = new Socket(InetAddress.getLocalHost(), 9500);
                log.debug("unirse");
                DataOutputStream datosenvio = new DataOutputStream(client.getOutputStream());
                datosenvio.writeUTF(objectMapper.writeValueAsString(this.datos));
                DataInputStream datosentrada = new DataInputStream(client.getInputStream());
                log.debug("entrada se conecto");
                Datos datosrecibidos = objectMapper.readValue(datosentrada.readUTF(), Datos.class);
                log.debug("se creo objeto");
                if (datosrecibidos.getRespueta().equals("jugada_correcta")) {
                    datos.setListacliente(datosrecibidos.getListacliente());
                    //aqui va la actualizacion del puntaje
                    labelturno.setText("En espera");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Palbras incorrectas");
//                    String palabraserrones=datosrecibidos.getListapalabras().obtenerstring();
//                    alert.setContentText("las siguientes palabras con incorrectas"+palabraserrones);
                    alert.showAndWait();
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void preguntar(){

    }


    public void clickon() {
        log.debug("si clickeaste compa");
        Ficha img = new Ficha(100,480,"Castillo1");
        img.setFitHeight(30);
        img.setFitWidth(30);
        img.setId("Imagen");
        String ID = img.getId();
        System.out.println(ID);
        this.juegopane.getChildren().add(img);
        img.setOnMousePressed(pressear);
        img.setOnMouseDragged(draggear);
        img.setOnMouseReleased(meter);
        System.out.println(img.letra);
    }

    EventHandler<MouseEvent> pressear =
            t -> {
                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
            };

    EventHandler<MouseEvent> draggear =
            t -> {
                ImageView img= (ImageView)(t.getSource());
                double offsetX = orgSceneX - t.getSceneX();
                double offsetY = orgSceneY - t.getSceneY();
                double newTranslateX = orgSceneX - offsetX -img.getFitWidth()/2;
                double newTranslateY = orgSceneY - offsetY -img.getFitHeight()/2;
                img.setX(newTranslateX);
                img.setY(newTranslateY);

            };
    EventHandler<MouseEvent> meter =
            t -> {
                Ficha img= (Ficha) (t.getSource());
                matriz.agregar(img);
            };
}
