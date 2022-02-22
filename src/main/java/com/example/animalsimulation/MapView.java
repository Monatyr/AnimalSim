package com.example.animalsimulation;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapView extends VBox {

    private Canvas canvas;

    private Toolbar toolbar;

    public HBox mapAndStats;
    public VBox allStats;

    public VBox animStats;
    public VBox currStats;
    public VBox trackStats;

    public Simulation simulation;

    private Simulator simulator;

    public Affine affine;

    public Vector2D currPos = null;

    private int applicationState = 0;                       //stan symulacji (w trakcie = 1, lub zatrzymana = 0)
    public Animal trackedAnimal = null;


    public MapView(Simulation simulation){
        this.simulation = simulation;
        this.canvas = new Canvas(500, 300);
        this.canvas.setOnMouseClicked(this::handleShowCell);
        this.toolbar = new Toolbar(this);

        this.currStats = new VBox();
        this.animStats = new VBox();
        this.allStats = new VBox();
        this.trackStats = new VBox();

        Text text0 = new Text("    Day: " + this.simulation.day);
        Text text1 = new Text("    Number of animals: " + this.simulation.map.animals.size());
        Text text2 = new Text("    Number of plants:  " + this.simulation.map.plants);
        Text text3 = new Text("    Dominant genepool: " + this.simulation.map.mostCommonGenepool());
        Text text4 = new Text("    Average energy:    " + this.simulation.map.averageEnergy());
        Text text5 = new Text("    Average life span: " + this.simulation.map.averageLifeSpan());
        Text text6 = new Text("    Average children no.: " + this.simulation.map.averageChildrenNumber());

        this.currStats.getChildren().addAll(text0, text1, text2, text3, text4, text5, text6);

        Text text8 = new Text("    Animal at: NaN");
        Text text9 = new Text("    Genepool: NaN");
        Text text10 = new Text("    Energy: NaN");
        Text text11 = new Text("\n");
        this.animStats.getChildren().addAll(text8, text9, text10, text11);


        Text text12 = new Text("    Current position: NaN");
        Text text13 = new Text("    Day of birth: NaN");
        Text text14 = new Text("    Day of death: NaN");
        Text text15 = new Text("    Number of children: NaN");
        Text text16 = new Text("    Number of descendants: NaN");
        this.trackStats.getChildren().addAll(text12, text13, text14, text15, text16);

        this.mapAndStats = new HBox();

        this.allStats.getChildren().addAll(this.currStats, this.animStats, this.trackStats);
        mapAndStats.getChildren().addAll(this.canvas, this.allStats);
        this.getChildren().addAll(this.toolbar, this.mapAndStats);

        this.affine = new Affine();
        this.affine.appendScale(this.canvas.getWidth()/this.simulation.map.width, this.canvas.getHeight()/this.simulation.map.height);
    }

    private Vector2D handleShowCell(MouseEvent event) {                             //funkcja zwracająca współrzędne komórki, na którą kliknął użytkownik
        double mouseX = event.getX();
        double mouseY = event.getY();

        try {

            Vector2D tempV = new Vector2D((int)this.affine.inverseTransform(mouseX, mouseY).getX(),
                    (int)(this.simulation.map.height - affine.inverseTransform(mouseX, mouseY).getY()));


            if(this.simulation.map.cells.containsKey(tempV)){
                if(this.simulation.map.cells.get(tempV).hasAnimals()){
                    this.animStats.getChildren().clear();

                    Text text1 = new Text("    Animal at: (" + tempV.x + ", " + tempV.y + ")");
                    Text text2 = new Text("    Genepool: " + this.simulation.map.cells.get(tempV).strongestAnimal().genepoolToString());
                    Text text3 = new Text("    Energy: " + this.simulation.map.cells.get(tempV).strongestAnimal().energy);
                    Text text4 = new Text("\n");

                    this.animStats.getChildren().addAll(text1, text2, text3, text4);

                    this.currPos = tempV;
                }
            }

        } catch (NonInvertibleTransformException e) {
            System.out.println("Error");
        }

        return null;
    }

    public void draw(){                                                 //funkcja rysująca mapę oraz statystyki
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setTransform(this.affine);

        g.setFill(Color.CHARTREUSE);
        g.fillRect(0,0, 500, 300);

        for(int i = this.simulation.map.jungleLowerLeft.y; i <= this.simulation.map.jungleUpperRight.y; i++){
            g.setFill(Color.OLIVEDRAB);
            for(int j = this.simulation.map.jungleLowerLeft.x; j <= this.simulation.map.jungleUpperRight.x; j++){
                if(simulation.map.cells.containsKey(new Vector2D(j, i))){
                    if(simulation.map.cells.get(new Vector2D(j,i)).hasPlant()){
                        g.setFill(Color.DARKGREEN);
                        g.fillRect(j, this.simulation.map.height - 1 - i, 1,1);
                        g.setFill(Color.OLIVEDRAB);
                    }
                }
                g.fillRect(j,this.simulation.map.height - 1 - i, 1, 1);
            }
        }

        g.setFill(Color.BROWN);
        for(int i = 0; i < simulation.map.height; i++) {
            for (int j = 0; j < simulation.map.width; j++) {
                if (simulation.map.cells.containsKey(new Vector2D(j, i))) {
                    if (simulation.map.cells.get(new Vector2D(j, i)).hasPlant()) {
                        g.setFill(Color.DARKGREEN);
                        g.fillRect(j, this.simulation.map.height - 1 - i, 1, 1);
                        g.setFill(Color.BROWN);
                    }
                    if(simulation.map.cells.get(new Vector2D(j,i)).hasAnimals()) {
                        g.setFill(simulation.map.cells.get(new Vector2D(j, i)).strongestAnimal().getColor());
                        g.fillOval(j, this.simulation.map.height - 1 - i, 1, 1);
                        g.setFill(Color.BROWN);
                    }
                }
            }
        }

        if(this.trackedAnimal != null) {
            g.setFill(Color.BLUE);
            g.fillOval(this.trackedAnimal.getPostition().x, this.simulation.map.height - 1 - this.trackedAnimal.getPostition().y, 1 ,1);
            g.setFill(Color.BROWN);
            this.updateTrackedAnimal();
        }


        this.currStats.getChildren().clear();

        Text text0 = new Text("    Day: " + this.simulation.map.day);
        Text text1 = new Text("    Number of animals: " + this.simulation.map.animals.size());
        Text text2 = new Text("    Number of plants:  " + this.simulation.map.plants);
        Text text3 = new Text("    Dominant genepool: " + this.simulation.map.mostCommonGenepool());
        Text text4 = new Text("    Average energy:    " + this.simulation.map.averageEnergy());
        Text text5 = new Text("    Average life span: " + this.simulation.map.averageLifeSpan());
        Text text6 = new Text("    Average children no.: " + this.simulation.map.averageChildrenNumber());
        Text text7 = new Text("\n");

        this.currStats.getChildren().addAll(text0, text1, text2, text3, text4, text5, text6, text7);


        g.setStroke(Color.BLACK);
        g.setLineWidth(0.05f);
        for(int i = 0; i <= this.simulation.map.width; i++){
            g.strokeLine(i, 0, i, this.simulation.map.height);
        }

        for(int i = 0; i <= this.simulation.map.height; i++){
            g.strokeLine(0, i, this.simulation.map.width, i);
        }

    }


    public void highlightGenepool(Vector2D vector){                     //funkcja podświetlająca na fioletowo zwierzęta o dominującym genotypie
        GraphicsContext g = this.canvas.getGraphicsContext2D();         //wywoływana przyciskiem Most common genepool
        g.setTransform(this.affine);

        g.setFill(Color.PURPLE);

        g.fillOval(vector.x, this.simulation.map.height - 1 - vector.y, 1, 1);
    }


    public void startTrackingAnimal(Animal animal){
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setTransform(this.affine);

        if(animal == this.trackedAnimal){
            g.setFill(animal.getColor());
            g.fillOval(animal.getPostition().x, this.simulation.map.height - 1 - animal.getPostition().y, 1, 1);
            this.trackedAnimal = null;

            Text text1 = new Text("    Current position: NaN");
            Text text2 = new Text("    Day of birth: NaN");
            Text text3 = new Text("    Day of death: NaN");
            Text text4 = new Text("    Number of children: NaN");
            Text text5 = new Text("    Number of descendants: NaN");

            this.trackStats.getChildren().clear();
            this.trackStats.getChildren().addAll(text1, text2, text3, text4, text5);
            return;
        }

        if(this.trackedAnimal != null) {
            Animal currAnimal = this.simulation.map.cells.get(trackedAnimal.getPostition()).strongestAnimal();

            g.setFill(currAnimal.getColor());
            g.fillOval(currAnimal.getPostition().x, this.simulation.map.height - 1 -currAnimal.getPostition().y, 1, 1);
        }

        g.setFill(Color.BLUE);
        g.fillOval(animal.getPostition().x, this.simulation.map.height - 1 - animal.getPostition().y, 1, 1);

        this.trackedAnimal = animal;

        this.trackStats.getChildren().clear();

        String str;
        if(trackedAnimal.deathDay == -1)
            str = "NaN";
        else
            str = String.valueOf(animal.deathDay);

        Text text1 = new Text("    Current position: (" + trackedAnimal.getPostition().x + ", " + (trackedAnimal.getPostition().y) + ")");
        Text text2 = new Text("    Day of birth: " + trackedAnimal.birthDay);
        Text text3 = new Text("    Day of death: " + str);
        Text text4 = new Text("    Number of children: " + trackedAnimal.children.size());

        this.trackedAnimal.descendants.clear();
        this.trackedAnimal.findDescendants(trackedAnimal);

        Text text5 = new Text("    Number of descendants: " + trackedAnimal.descendants.size());

        this.trackStats.getChildren().addAll(text1, text2, text3, text4, text5);
    }

    public void updateTrackedAnimal(){
        String str;
        if(trackedAnimal.deathDay == -1)
            str = "NaN";
        else {
            str = String.valueOf(this.trackedAnimal.deathDay);
        }
        Text text1 = new Text("    Current position: (" + trackedAnimal.getPostition().x + ", " + (trackedAnimal.getPostition().y) + ")");
        Text text2 = new Text("    Day of birth: " + trackedAnimal.birthDay);
        Text text3 = new Text("    Day of death: " + str);
        Text text4 = new Text("    Number of children: " + trackedAnimal.children.size());

        this.trackedAnimal.descendants.clear();
        this.trackedAnimal.findDescendants(trackedAnimal);

        Text text5 = new Text("    Number of descendants: " + trackedAnimal.descendants.size());


        this.trackStats.getChildren().clear();
        this.trackStats.getChildren().addAll(text1, text2, text3, text4, text5);

        if(this.trackedAnimal.deathDay != -1)
            this.trackedAnimal = null;
    }


    public Simulation getSimulation() {
        return this.simulation;
    }


    public void setApplicationState(int state){
        if (state == this.applicationState){
            return;
        }

        if(state == 1){
            this.simulator = new Simulator(this, this.simulation);
        }

        this.applicationState = state;
    }

    public Simulator getSimulator(){
        return this.simulator;
    }

}

