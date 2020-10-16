/**
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.mycompany.app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.FontStyle;
import com.google.gson.Gson;

public class App extends Application {

    private MapView mapView;
    private Button searchIpAddressBtn;
    private TextField searchBox;
    private IpAddressModel ipAddressInfo;
    private Gson gson;
    private Point location;
    private GraphicsOverlay graphicsOverlay;
    private final int hexRed = 0xFFFF0000;
    private final int hexBlue = 0xFF0000FF;

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        // set the title and size of the stage and show it
        stage.setTitle("IP Address Tracker");
        stage.setWidth(815);
        stage.setHeight(700);
        stage.show();
        
        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        
        
        // create a MapView to display the map and add it to the stack pane
        mapView = new MapView();
        
        //Nodes
        searchIpAddressBtn = new Button("Buscar...");
        searchIpAddressBtn.setDefaultButton(true);
        searchIpAddressBtn.autosize();
        StackPane.setAlignment(searchIpAddressBtn, Pos.TOP_LEFT);
        StackPane.setMargin(searchIpAddressBtn, new Insets(10, 0, 0, 5));
        
        searchBox = new TextField();
        searchBox.setPromptText("IP Address");
        searchBox.setEditable(true);
        searchBox.setMaxWidth(260.0);
        StackPane.setAlignment(searchBox, Pos.TOP_LEFT);
        StackPane.setMargin(searchBox, new Insets(10, 0, 0, 61));
        
        
        stackPane.getChildren().addAll(mapView, searchBox, searchIpAddressBtn);
        
        
        setupMap();
        setupGraphicsOverlay();
        listeners();
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }
    
    private void setupMap() { // This method will initialize the map with a basemap and the designated area of interest and add the map to the map view.
        if (mapView != null) {
            Basemap.Type basemapType = Basemap.Type.OPEN_STREET_MAP; 
            double latitude = 19.432649;
            double longitude = -99.133146;
            int levelOfDetail = 5;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mapView.setMap(map);
        }
    }
    
    private void listeners() {
		EventHandler<ActionEvent> search = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				String ipAddress = searchBox.getText();
				System.out.println("IP Address: " + ipAddress);
				try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://api.ip2location.com/v2/?key=demo&ip="+ipAddress+"&package=WS5&format=json&addon=country,region,city&lang=en").openStream(), "UTF-8").useDelimiter("\\A")) {
					gson = new Gson();
					ipAddressInfo = gson.fromJson(s.next(), IpAddressModel.class);
					location = new Point(ipAddressInfo.getLongitude(), ipAddressInfo.getLatitude());
					graphicsOverlay.getGraphics().clear();
					addPointGraphic(location);
					addInformationLocation();
				} catch (java.io.IOException e) {
					e.printStackTrace();
				}
				
			}
		};
		
		searchIpAddressBtn.addEventFilter(ActionEvent.ACTION, search);
	}
    
    private void setupGraphicsOverlay() {
    	  if (mapView != null) {
    	    graphicsOverlay = new GraphicsOverlay();
    	    mapView.getGraphicsOverlays().add(graphicsOverlay);
    	  }
    	}
    
    private void addPointGraphic(Point location) {
    	  if (graphicsOverlay != null) {
    	    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexRed, 10.0f);
    	    pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
    	    Point point = new Point(location.getX(), location.getY(), SpatialReferences.getWgs84());
    	    Graphic pointGraphic = new Graphic(point, pointSymbol);
    	    graphicsOverlay.getGraphics().add(pointGraphic);
    	  }
    	}
    
    private void addInformationLocation() {
		if (mapView != null) {
			String info = ipAddressInfo.getCity_name()+", "+ipAddressInfo.getRegion_name()+", "+ipAddressInfo.getCountry_name();
			TextSymbol textSymbol = new TextSymbol(10, info, hexBlue, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.TOP);
			textSymbol.setFontStyle(FontStyle.ITALIC);
			Point basePoint = new Point(location.getX()+0.05, location.getY(), SpatialReferences.getWgs84());
			Graphic textGraphic = new Graphic(basePoint, textSymbol);
			graphicsOverlay.getGraphics().add(textGraphic);
			
		}
	}
    
    
    
    
}
