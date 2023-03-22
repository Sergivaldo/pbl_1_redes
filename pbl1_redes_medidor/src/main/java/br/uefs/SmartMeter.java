package br.uefs;

import br.uefs.util.DateTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class SmartMeter extends Thread {
    private String host;
    private int port;
    private long sendInteval;
    private float rate;
    private int code;
    private double consumption;
    private boolean configured;
    private static SmartMeter instance;
    private SmartMeterInterface smartMeterInterface;

    public SmartMeter() {
        this.smartMeterInterface = new SmartMeterInterface();
        this.rate = 1;
    }

    /**
     * Inicia o medidor, executando  em threads diferentes a geração de consumos e a interface.
     * O medidor só irá enviar dados para o servidor após ser configurado.
     */
    public void run() {
        smartMeterInterface.start();
        try {
            DatagramSocket ds = new DatagramSocket();
            while (true) {
                Thread.sleep(sendInteval);
                if (configured) {
                    sendConsumption(ds);
                }
            }
            //Fecha o DatagramSocket
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public SmartMeterInterface getSmartMeterInterface() {
        return smartMeterInterface;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    /**
     * Envia os consumos gerados pelo medidor para o servidor através
     * de um socket UDP.
     *
     * @param ds Socket UDP
     * @throws UnknownHostException Caso seja passado um host inválido.
     * @throws IOException Caso ocorra erro de I/O.
     */
    private void sendConsumption(DatagramSocket ds) throws UnknownHostException, IOException {
        InetAddress addr = InetAddress.getByName(host);
        BigDecimal consumption = new BigDecimal((new Random().nextFloat()) * rate).setScale(2, RoundingMode.HALF_UP);
        StringBuilder builder = new StringBuilder();
        builder.delete(0, builder.length());
        builder.append(Integer.toString(code) + ";");
        builder.append(consumption.toString() + ";");
        builder.append(DateTime.currentDateTime());
        byte[] msg = builder.toString().getBytes();

        DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);
        ds.send(pkg);
    }

    private class SmartMeterInterface extends Thread {
        private Scanner in;

        public SmartMeterInterface() {
            in = new Scanner(System.in);
        }

        /***
         * Inicia a execução da interface do medidor. Caso o medidor não esteja configurado,
         * o menu de configurações é mostrado. Caso o usuário insira um dado inválido, uma exceção
         * é lançada e o buffer do teclado é limpado.
         */
        public void run() {
            while (true) {
                try{
                    if (!configured) {
                        settingsMenu(in);
                    }
                    Menu(in);
                }catch (InputMismatchException e){
                    in.nextLine();
                }

            }
        }

        /***
         * Menu de configuarações do medidor.
         *
         * @param in - Objeto da classe scanner que ler dados do teclado
         *           do usuário.
         */
        private void settingsMenu(Scanner in) {
            clearInterface();
            System.out.print("Código do medidor(apenas números): ");
            code = in.nextInt();
            System.out.print("\nHost: ");
            in.nextLine();
            host = in.nextLine();
            System.out.print("\nPorta(apenas números): ");
            port = in.nextInt();
            System.out.print("\nIntervalo de medição(seg): ");
            sendInteval = 1000 * in.nextInt();
            configured = true;
        }

        /***
         * Menu principal do medidor com opções de definição
         * do consumo de energia e configurações do medidor
         *
         * @param in - Objeto da classe scanner que ler dados do teclado
         *          do usuário.
         */
        private void Menu(Scanner in) {
            clearInterface();
            System.out.println("(1) CONSUMO DE ENERGIA");
            System.out.println("(2) CONFIGURAÇÕES");
            int options = in.nextInt();
            switch (options) {
                case 1:
                    consumptionMenu(in);
                    break;
                case 2:
                    settingsMenu(in);
            }
        }

        /***
         * Menu de definição da taxa de consumo de energia.
         *
         * @param in- Objeto da classe scanner que ler dados do teclado
         *          do usuário.
         */
        private void consumptionMenu(Scanner in){
            System.out.print("\nTaxa de consumo de energia(apenas números): ");
            rate = in.nextFloat();
        }

        /***
         * 'Limpa' a interface do usuário utilizando quebra de linha
         */
        private void clearInterface(){
            for(int i=0;i<30;i++){
                System.out.print("\n");
            }
        }
    }
}
