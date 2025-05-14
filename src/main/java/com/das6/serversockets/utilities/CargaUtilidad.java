package com.das6.serversockets.utilities;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class CargaUtilidad {
    // Mapa para almacenar el ProgressIndicator de cada panel
    private static Map<Pane, ProgressIndicator> progressIndicators = new HashMap<>();

    /**
     * Muestra un indicador de carga en el panel especificado con una posición y tamaño personalizados.
     *
     * @param panel   El panel en el que se añadirá el indicador de carga.
     * @param layoutX La posición en el eje X donde se colocará el indicador de carga dentro del panel.
     * @param layoutY La posición en el eje Y donde se colocará el indicador de carga dentro del panel.
     * @param maxSize El tamaño máximo del indicador de carga.
     */
    public static void mostrarIndicadorDeCarga(Pane panel, double layoutX, double layoutY, double maxSize) {
        if (!progressIndicators.containsKey(panel)) {
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxSize(maxSize, maxSize);
            progressIndicator.setLayoutX(layoutX);
            progressIndicator.setLayoutY(layoutY);
            panel.getChildren().add(progressIndicator);

            // Guarda el indicador en el mapa y también en el UserData del panel
            progressIndicators.put(panel, progressIndicator);
            panel.setUserData(progressIndicator);
        }
    }

    /**
     * Muestra un indicador de carga en el panel especificado en la posición predeterminada.
     *
     * @param panel El panel en el que se añadirá el indicador de carga.
     */
    public static void mostrarIndicadorDeCarga(Pane panel) {
        if (!progressIndicators.containsKey(panel)) {
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxSize(90, 90);
            progressIndicator.setLayoutX(531); // Posición predeterminada en X
            progressIndicator.setLayoutY(340); // Posición predeterminada en Y
            panel.getChildren().add(progressIndicator);

            // Guarda el indicador en el mapa y en UserData
            progressIndicators.put(panel, progressIndicator);
            panel.setUserData(progressIndicator);
        }
    }

    /**
     * Oculta el indicador de carga en el panel especificado.
     *
     * @param panel El panel del que se removerá el indicador de carga.
     */
    public static void ocultarIndicadorDeCarga(Pane panel) {
        // Obtiene el indicador del mapa y verifica si existe
        ProgressIndicator progressIndicator = progressIndicators.remove(panel);
        if (progressIndicator != null) {
            panel.getChildren().remove(progressIndicator);
            panel.setUserData(null); // Limpia UserData para evitar referencias cruzadas
        }
    }
}

