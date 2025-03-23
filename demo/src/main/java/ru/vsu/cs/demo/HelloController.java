package ru.vsu.cs.demo;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import guru.nidi.graphviz.engine.*;
import java.io.*;
import java.util.*;

public class HelloController {

    @FXML
    private TextField keyField;
    @FXML
    private TextField valueField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label mapLabel;
    @FXML
    private ScrollPane mapScrollPane;
    @FXML
    private ImageView graphView;
    @FXML
    private ProgressIndicator graphProgress;
    @FXML
    private ProgressIndicator statusProgress;

    private HashMultiMap<String, String> map = new HashMultiMap<>();
    private final GraphvizGenerator<String, String> graphvizGenerator = new GraphvizGenerator<>();

    //Обработчики событий кнопок
    @FXML
    private void onAddClick() {
        String key = keyField.getText();
        String value = valueField.getText();
        if (!key.isEmpty() && !value.isEmpty()) {
            map.put(key, value);
            updateUI("Добавлено: " + key + " → " + value);
            clearFields();
        } else {
            setStatus("Ошибка: поля не заполнены");
        }
    }

    @FXML
    private void onRemoveClick() {
        String key = keyField.getText();
        String value = valueField.getText();
        if (!key.isEmpty() && !value.isEmpty()) {
            if (map.remove(key, value)) {
                updateUI("Удалено: " + key + " → " + value);
            } else {
                setStatus("Ошибка: элемент не найден");
            }
            clearFields();
        } else {
            setStatus("Ошибка: поля не заполнены");
        }
    }

    @FXML
    private void onClearClick() {
        map.clear();
        updateUI("Мапа очищена");
        clearFields();
    }

    @FXML
    private void onSaveClick() {
        System.out.println("Сохранение запущено");
        setStatusWithProgress("Сохранение...");
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Попытка сохранения в: assets/map.dat");
                    SerializationHelper.serialize(map, "assets/map.dat");
                    try (FileWriter fw = new FileWriter("assets/style.txt");
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write(graphvizGenerator.getStyle());
                    }
                    System.out.println("Сохранение успешно");
                    return null; // Это и есть missing return statement
                } catch (Exception e) {
                    System.err.println("Ошибка при сохранении:");
                    e.printStackTrace();
                    throw e;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    setStatus("Мапа сохранена");
                    statusProgress.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setStatus("Ошибка сохранения");
                    statusProgress.setVisible(false);
                });
            }
        };
        new Thread(saveTask).start();
    }


    @FXML
    private void onLoadClick() {
        System.out.println("Загрузка запущена");
        setStatusWithProgress("Загрузка...");
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Попытка загрузки из: assets/map.dat");
                    map = SerializationHelper.deserialize("assets/map.dat");
                    try (FileReader fr = new FileReader("assets/style.txt");
                         BufferedReader br = new BufferedReader(fr)) {
                        graphvizGenerator.setGraphStyle(br.readLine());
                    }
                    System.out.println("Загрузка успешна");
                    return null; // Это и есть missing return statement
                } catch (Exception e) {
                    System.err.println("Ошибка при загрузке:");
                    e.printStackTrace();
                    throw e;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    updateUI("Мапа загружена");
                    statusProgress.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setStatus("Ошибка загрузки");
                    statusProgress.setVisible(false);
                });
            }
        };
        new Thread(loadTask).start();
    }


    @FXML
    private void onStyleClick() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Классический", Arrays.asList(
                "Классический",
                "Вертикальный",
                "Горизонтальный",
                "Круглые узлы",
                "Свой стиль"
        ));
        dialog.setTitle("Выбор стиля графа");
        dialog.setHeaderText("Выберите стиль графа");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(style -> {
            String graphStyle = switch (style) {
                case "Классический" -> "digraph G {\nrankdir=LR;\nnode[shape=box];\n";
                case "Вертикальный" -> "digraph G {\nrankdir=LR;\nnode[shape=box];\n";
                case "Горизонтальный" -> "digraph G {\nrankdir=LB;\nnode[shape=box];\n";
                case "Круглые узлы" -> "digraph G {\nrankdir=LR;\nnode[shape=circle];\n";
                default -> "digraph G {\nrankdir=LR;\nnode[shape=box];\n";
            };

            if (style.equals("Свой стиль")) {
                TextInputDialog textDialog = new TextInputDialog(graphStyle);
                textDialog.setTitle("Настройка стиля графа");
                textDialog.setHeaderText("Введите стиль графа в формате Graphviz");
                Optional<String> textResult = textDialog.showAndWait();
                textResult.ifPresent(textStyle -> {
                    graphvizGenerator.setGraphStyle(textStyle);
                    updateGraph();
                    setStatus("Стиль графа обновлен");
                });
            } else {
                graphvizGenerator.setGraphStyle(graphStyle);
                updateGraph();
                setStatus("Стиль графа обновлен");
            }
        });
    }

    @FXML
    private void onIsEmptyCheck() {
        setStatus("Пустота: " + map.isEmpty());
    }

    @FXML
    private void onContainsKeyCheck() {
        setStatus("Ключ " + keyField.getText() + ": " + map.containsKey(keyField.getText()));
    }

    @FXML
    private void onContainsValueCheck() {
        setStatus("Значение " + valueField.getText() + ": " + map.containsValue(valueField.getText()));
    }

    @FXML
    private void onGetKeySet() {
        Set<String> keys = map.keySet();
        StringBuilder sb = new StringBuilder("Ключи:\n");
        keys.forEach(k -> sb.append(k).append("\n"));
        setStatus(sb.toString());
    }

    @FXML
    private void onGetEntrySet() {
        Set<Map.Entry<String, Collection<String>>> entries = map.entrySet();
        StringBuilder sb = new StringBuilder("Записи:\n");
        entries.forEach(e -> sb.append(e.getKey()).append(" → ").append(e.getValue()).append("\n"));
        setStatus(sb.toString());
    }

    @FXML
    private void onPutAllMap() {
        HashMultiMap<String, String> otherMap = new HashMultiMap<>();
        otherMap.put("test", "value1");
        otherMap.put("test", "value2");
        map.putAll(otherMap);
        updateUI();
    }

    @FXML
    private void onPutAllJavaMap() {
        Map<String, Collection<String>> javaMap = new HashMap<>();
        javaMap.put("java", Arrays.asList("value1", "value2"));
        map.putAll(javaMap);
        updateUI();
    }

    @FXML
    private void onReplaceValues() {
        map.replaceAllValues("test", Arrays.asList("new_value"));
        updateUI();
    }

    @FXML
    private void onAddValues() {
        map.addAllValues("test", Arrays.asList("additional_value"));
        updateUI();
    }

    @FXML
    private void onRemoveKey() {
        String key = keyField.getText();
        if (map.removeKey(key)) {
            setStatus("Ключ " + key + " удален");
            updateUI();
        } else {
            setStatus("Ключ не найден");
        }
    }

    // Вспомогательные методы
    private void updateUI(String status) {
        updateMapLabel();
        setStatus(status);
    }

    private void updateUI() {
        updateMapLabel();
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void setStatusWithProgress(String text) {
        statusLabel.setText(text);
        statusProgress.setVisible(true);
    }

    private void updateMapLabel() {
        StringBuilder sb = new StringBuilder("Текущая мапа:\n");
        map.entrySet().forEach(entry -> {
            sb.append(entry.getKey()).append(" → ").append(entry.getValue()).append("\n");
        });
        mapLabel.setText(sb.toString());
        mapScrollPane.setFitToWidth(true);
        mapScrollPane.setFitToHeight(true);
        updateGraph();
    }

    private void clearFields() {
        keyField.clear();
        valueField.clear();
    }

    private void updateGraph() {
        graphProgress.setVisible(true);
        Task<Void> renderTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String dot = graphvizGenerator.toGraphviz(map);
                    Graphviz.fromString(dot)
                            .render(Format.PNG)
                            .toFile(new File("assets/temp_graph.png"));
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException("Graphviz error", e);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Image fxImage = new Image(new File("assets/temp_graph.png").toURI().toString());
                    graphView.setImage(fxImage);
                    graphProgress.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setStatus("Ошибка генерации графа");
                    graphProgress.setVisible(false);
                });
            }
        };
        new Thread(renderTask).start();
    }
}
