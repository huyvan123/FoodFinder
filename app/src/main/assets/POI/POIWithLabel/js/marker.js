var kMarker_AnimationDuration_ChangeDrawable = 500;
var kMarker_AnimationDuration_Resize = 1000;
function Marker(poiData) {

    /*
        For creating the marker a new object AR.GeoObject
        will be created at the specified geolocation.
         An AR.GeoObject connects one or more AR.GeoLocations with multiple AR.Drawables.
          The AR.Drawables can be defined for multiple targets. A target can be the camera,
           the radar or a direction indicator. Both the radar and direction indicators will be covered in more detail in later examples.
    */

    this.poiData = poiData;
    this.isSelected = false;

        /*
            With AR.PropertyAnimations you are able to animate almost any property of ARchitect objects.
            This sample will animate the opacity of both background drawables so that one will
            fade out while the other one fades in. The scaling is animated too. The marker size changes
            over time so the labels need to be animated too in order to keep them relative to the
            background drawable. AR.AnimationGroups are used to synchronize all animations in parallel or sequentially.
        */

    this.animationGroup_idle = null;
    this.animationGroup_selected = null;

    var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);

    /*
        There are two major points that need to be considered while drawing multiple AR.
        Drawables at the same location. It has to be defined which one is before or behind
         another drawable (rendering order) and if they need a location offset.
         For both scenarios, ARchitect has some functionality to adjust the drawable behavior.

        To position the AR.Label in front of the background, the background drawable(AR.ImageDrawable)
        receives a zOrder of 0. Both labels have a zOrder of 1. This way it is guaranteed that the labels
         will be drawn in front of the background drawable.

        Assuming both labels will be drawn on the same geolocation connected with the same AR.
        GeoObject they will overlap. To adjust their position change the offsetX and offsetY property of an AR.
        Drawable object. The unit for offsets are SDUs. For more information about SDUs look up the
         code reference or the online documentation.

        In the following both AR.Labels are initialized and positioned. Note that they are
        added to the cam property of the AR.GeoObject the same way as an AR.ImageDrawable.
    */
    this.markerDrawable_idle = new AR.ImageDrawable(World.markerDrawable_idle, World.foodstore_height, {
        zOrder: 0,
        opacity: 1.0,
        /*
            To react on user interaction, an onClick property can be set for each AR.Drawable. The property is a function which will be called each time the user taps on the drawable. The function called on each tap is returned from the following helper function defined in marker.js. The function returns a function which checks the selected state with the help of the variable isSelected and executes the appropriate function. The clicked marker is passed as an argument.
        */
        onClick: Marker.prototype.getOnClickTrigger(this)
    });
    // create an AR.ImageDrawable for the marker in selected state
    this.markerDrawable_selected = new AR.ImageDrawable(World.markerDrawable_idle, 2.5, {
        zOrder: 0,
        opacity: 0.0,
        onClick: null
    });

    //adding radar
    this.radarCircle = new AR.Circle(0.03, {
            horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
            opacity: 0.8,
            style: {
                fillColor: "#ffffff"
            }
        });

        this.radarCircleSelected = new AR.Circle(0.05, {
                horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
                opacity: 0.8,
                style: {
                    fillColor: "#0066ff"
                }
            });

        this.radardrawables = [];
        this.radardrawables.push(this.radarCircle);
        this.radardrawablesSelected = [];
        this.radardrawablesSelected.push(this.radarCircleSelected);
    //end add

    this.directionIndicatorDrawable = new AR.ImageDrawable(World.markerDrawable_directionIndicator, 0.1, {
            enabled: true,
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
    });

    // Changed: 
    this.markerObject = new AR.GeoObject(markerLocation, {
        drawables: {
            cam: [this.markerDrawable_idle],
            indicator: this.directionIndicatorDrawable,
            radar: this.radardrawables
        }
    });

    return this;

}

Marker.prototype.getOnClickTrigger = function(marker) {

    /*
        The setSelected and setDeselected functions are prototype Marker functions.
        Both functions perform the same steps but inverted.
    */

    return function() {

        if (!Marker.prototype.isAnyAnimationRunning(marker)) {
            if (marker.isSelected) {

                Marker.prototype.setDeselected(marker);

            } else {
                Marker.prototype.setSelected(marker);
                try {
                    World.onMarkerSelected(marker);
                } catch (err) {
                    alert(err);
                }

            }
        } else {
            AR.logger.debug('a animation is already running');
        }


        return true;
    };
};

Marker.prototype.setSelected = function(marker) {

    marker.isSelected = true;

    if (marker.animationGroup_selected === null) {

        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.2
        var selectedDrawableResizeAnimationX = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.x', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.2
        var selectedDrawableResizeAnimationY = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.y', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        /*
         There are two types of AR.AnimationGroups. Parallel animations are running at the same time, sequentials are played one after another. This example uses a parallel AR.AnimationGroup.
         */
        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [selectedDrawableResizeAnimationX, selectedDrawableResizeAnimationY]);
    }

    // removes function that is set on the onClick trigger of the idle-state marker
    marker.markerDrawable_idle.onClick = null;
    // sets the click trigger function for the selected state marker
    marker.markerDrawable_selected.onClick = Marker.prototype.getOnClickTrigger(marker);

    marker.markerObject.drawables.radar = marker.radardrawablesSelected;

    // starts the selected-state animation
    marker.animationGroup_selected.start();
};

Marker.prototype.setDeselected = function(marker) {

    marker.isSelected = false;

    marker.markerObject.drawables.radar = marker.radardrawables;

    if (marker.animationGroup_idle === null) {

        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.0
        var selectedDrawableResizeAnimationX = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.x', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.0
        var selectedDrawableResizeAnimationY = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.y', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        /*
         There are two types of AR.AnimationGroups. Parallel animations are running at the same time, sequentials are played one after another. This example uses a parallel AR.AnimationGroup.
         */
        marker.animationGroup_idle = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [selectedDrawableResizeAnimationX, selectedDrawableResizeAnimationY]);
    }

    // sets the click trigger function for the idle state marker
    marker.markerDrawable_idle.onClick = Marker.prototype.getOnClickTrigger(marker);
    // removes function that is set on the onClick trigger of the selected-state marker
    marker.markerDrawable_selected.onClick = null;
    // starts the idle-state animation
    marker.animationGroup_idle.start();
};

Marker.prototype.isAnyAnimationRunning = function(marker) {

    if (marker.animationGroup_idle === null || marker.animationGroup_selected === null) {
        return false;
    } else {
        if ((marker.animationGroup_idle.isRunning() === true) || (marker.animationGroup_selected.isRunning() === true)) {
            return true;
        } else {
            return false;
        }
    }
};
