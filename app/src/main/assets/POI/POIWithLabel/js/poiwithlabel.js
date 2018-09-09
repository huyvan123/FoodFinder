
var PoiRadar = {

		hide: function hideFn() {
			AR.radar.enabled = false;
		},

		show: function initFn() {

			// the div defined in the index.htm
			AR.radar.container = document.getElementById("radarContainer");

			// set the back-ground image for the radar
			AR.radar.background = new AR.ImageResource("assets/radar_bg.png");

			// set the north-indicator image for the radar (not necessary if you don't want to display a north-indicator)
			AR.radar.northIndicator.image = new AR.ImageResource("assets/radar_north.png");

			// center of north indicator and radar-points in the radar asset, usually center of radar is in the exact middle of the bakground, meaning 50% X and 50% Y axis --> 0.5 for centerX/centerY
			AR.radar.centerX = 0.5;
			AR.radar.centerY = 0.5;

			AR.radar.radius = 0.3;
			AR.radar.northIndicator.radius = 0.0;

			AR.radar.enabled = true;
		},

		updatePosition: function updatePositionFn() {
			if (AR.radar.enabled) {
				AR.radar.notifyUpdateRadarPosition();
			}
		},

		// you may define some custom action when user pressed radar, e.g. display distance, custom filtering etc.
		clickedRadar: function clickedRadarFn() {
			alert("Food Store Radar");
		},

		setMaxDistance: function setMaxDistanceFn(maxDistanceMeters) {
			AR.radar.maxDistance = maxDistanceMeters;
		}
	};
// implementation of AR-Experience (aka "World")
var World = {
	// true once data was fetched
	initiallyLoadedData: false,

	//set marker location is true
	isSetMarkerLocaion:false,

    //marker info
	foodstore_description:null,

	foodstore_title:null,

	foodstore_height: 0,

	foodstore_id:null,

	foodstore_lat:0,

	foodstore_lng:0,

	currentMarker: null,

	//update distance
	locationUpdateCounter: 0,
    updatePlacemarkDistancesEveryXLocationUpdates: 10,

	// POI-Marker asset
	markerDrawable_idle: null,

	//POI director
	markerDrawable_directionIndicator: null,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

		// show radar & set click-listener
		PoiRadar.show();
		$('#radarContainer').unbind('click');
		$("#radarContainer").click(PoiRadar.clickedRadar);

		/*
			The example Image Recognition already explained how images are loaded and displayed in the augmented reality view. This sample loads an AR.ImageResource when the World variable was defined. It will be reused for each marker that we will create afterwards.
		*/
		World.markerDrawable_idle = new AR.ImageResource("assets/restaurant.png");

        // Create an AR.ImageResource referencing the image that should be displayed for a direction indicator.
        World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png");

		/*
			Since there are additional changes concerning the marker it makes sense to extract the code to a separate Marker class (see marker.js). Parts of the code are moved from loadPoisFromJsonData to the Marker-class: the creation of the AR.GeoLocation, the creation of the AR.ImageDrawable and the creation of the AR.GeoObject. Then instantiate the Marker in the function loadPoisFromJsonData:
		*/
		var marker = new Marker(poiData);

		// updates distance information of all placemarks
        World.updateDistanceToUserValues(marker);

		// Updates status message as a user feedback that everything was loaded properly.
		World.updateStatusMessage('1 place loaded');
	},

	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
	updateDistanceToUserValues: function updateDistanceToUserValuesFn(marker) {
	    marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
	},


	// updates status message shown in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	// fired when user pressed maker in cam
    onMarkerSelected: function onMarkerSelectedFn(marker) {
    	World.currentMarker = marker;

    	// update panel values
    	$("#poi-detail-title").html(marker.poiData.title);
    	$("#poi-detail-description").html(marker.poiData.description);

    	/* It's ok for AR.Location subclass objects to return a distance of `undefined`. In case such a distance was calculated when all distances were queried in `updateDistanceToUserValues`, we recalcualte this specific distance before we update the UI. */
    	if( undefined == marker.distanceToUser ) {
    		marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
    	}
    	var distanceToUserValue = (marker.distanceToUser > 999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");

    	$("#poi-detail-distance").html(distanceToUserValue);

    	// show panel
    	$("#panel-poidetail").panel("open", 123);

    	$( ".ui-panel-dismiss" ).unbind("mousedown");

    	$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
    		World.currentMarker.setDeselected(World.currentMarker);
    	});
    },

    //set marker location
    setMarkerLocation: function setMarkerLocationFn(id, title, description, height, lat, lng){
        World.foodstore_id = id;
        World.foodstore_lat = lat;
        World.foodstore_lng = lng;
        World.foodstore_title = title;
        World.foodstore_description = description;
        World.foodstore_height = height;
        World.isSetMarkerLocaion = true;
    },

	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {
         console.log("fukjs lat: " + lat);
         console.log("fukjs lng: " + lon);
         console.log("fukjs foodstore_id" + World.foodstore_id);
         console.log("fukjs foodstore_title" + World.foodstore_title);
         console.log("fukjs foodstore_description" + World.foodstore_description);
         console.log("fukjs foodstore_height" + World.foodstore_height);
		/*
			The custom function World.onLocationChanged checks with the flag World.initiallyLoadedData
			 if the function was already called. With the first call of World.onLocationChanged an
			 object that contains geo information will be created which will be later used to create
			 a marker using the World.loadPoisFromJsonData function.
		*/
		if (!World.initiallyLoadedData && World.isSetMarkerLocaion) {
			// creates a poi object with a random location near the user's location
			var poiData = {
				"id": World.foodstore_id,
				"longitude": World.foodstore_lng,
				"latitude": World.foodstore_lat,
				"altitude": 0.0,
				"description": World.foodstore_description,
				"title": World.foodstore_title
			};

			World.loadPoisFromJsonData(poiData);
			World.initiallyLoadedData = true;
		}else if (World.locationUpdateCounter === 0) {
         	// update placemark distance information frequently, you max also update distances only every 10m with some more effort
         	World.updateDistanceToUserValues();
         }
         // helper used to update placemark information every now and then (e.g. every 10 location upadtes fired)
         World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
	},
};

/* 
	Set a custom function where location changes are forwarded to. There is also a possibility to set AR.context.onLocationChanged to null. In this case the function will not be called anymore and no further location updates will be received. 
*/


AR.context.onLocationChanged = World.locationChanged;
//AR.radar.container = document.getElementById("radarContainer");