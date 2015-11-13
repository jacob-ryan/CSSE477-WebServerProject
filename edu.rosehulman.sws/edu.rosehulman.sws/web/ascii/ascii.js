// CONSTANTS
var SPEEDS = [];
SPEEDS["Turbo"] = SPEEDS["turbo"] = SPEEDS["TURBO"] = 50;
SPEEDS["Normal"] = SPEEDS["normal"] = SPEEDS["NORMAL"] = 250;
SPEEDS["Slo-Mo"] = SPEEDS["slo-mo"] = SPEEDS["SLO-MO"] = 1000;
var DEFAULTSPEED = SPEEDS["normal"];
var DEFAULTFONTSIZE = "12pt"

var host = 'http://localhost'
var port = 8080
// var fullAddress = host + ':' + port + '/RestAPI/'
var fullAddress = '/RestAPI/'


//GLOBAL VARIABLES
var animationArray;
var totalFrames;
var currentFrame = 0;
var currentSpeed = DEFAULTSPEED;
var animationInterval;
var fullAnimation;


//run initial setup
window.onload = function()
{
	buttonSetup();
	dropDownSetup();
	radioButtonSetup();
	var textArea = document.getElementById("animation_space");
	textArea.value = '';
	textArea.style.fontSize = DEFAULTFONTSIZE;
	var browseButton = document.getElementById("browse");
	browseButton.onclick = function(){ browseHandler()};
	var getButton = document.getElementById("get");
	getButton.onclick = function(){ getHandler()};
	var updateButton = document.getElementById("update");
	updateButton.onclick = function(){ updateHandler()};
	var uploadButton = document.getElementById("upload");
	uploadButton.onclick = function(){ uploadHandler()};
	var deleteButton = document.getElementById("delete");
	deleteButton.onclick = function(){ deleteHandler()};
	var multiselect = document.getElementById("animationlist");
	multiselect.onchange = function(){ selectChangeHandler();}

};

// var listAnimations = [{'id': 1, 'name': 'first', 'dateCreated': '9/29/15', 'author': 'David Mutchler', 'description': 'A person dancing', 'animations': 'o\n|\n/_ \n=====\n	o\n	|\n	/_\n=====\no\n|\n/_\nplease\n=====\n	o\n	|\n	/_\nplease'}, 
// {'id': 2, 'name': 'second', 'dateCreated': '9/29/15', 'author': 'David Mutchler', 'description': 'A person dancing', 'animations': 'o\n|\n/_ \n=====\n	o\n	|\n	/_\n=====\no\n|\n/_\nplease\n=====\n	o\n	|\n	/_\nplease'}];
var listAnimations;

function selectChangeHandler()
{
	var multiselect = document.getElementById("animationlist");
	var targetID = multiselect.options[multiselect.selectedIndex].value;

    for (var i = 0; i < listAnimations.length; i++)
	{
		var obj = listAnimations[i];
    	var id = obj['id'];
    	if(id == targetID)
    	{
    		var name = obj['name'];
    		var author = obj['author'];
    		var description = obj['description'];
    		var dateCreated = obj['dateCreated'];

    		var nameField = document.getElementById("name");
    		var authorField = document.getElementById("author");
    		var descriptionField = document.getElementById("description");
    		var dateCreatedField = document.getElementById("dateCreated");

    		nameField.value = name;
    		authorField.value = author;
    		descriptionField.value = description;
    		dateCreatedField.value = dateCreated;
    	}
    }

};

function browseHandler()
{
	console.log("GET AllAnimations");
	var multiselect = document.getElementById('animationlist');
	multiselect.innerHTML = "";
	
	$.ajax({
  url: fullAddress + 'AllAnimations',
  type: 'GET',
  headers: { 
  	'dataType': 'json',
  	'Access-Control-Allow-Origin': "*"
  },
  
  
  success:  function( data )
   {
   	data = JSON.parse(data);
   	listAnimations = data;
   	for (var i = 0; i < data.length; i++) 
  	{
  		var obj = data[i];
  		var name = obj.name;
    	var option = document.createElement('option');
    	option.innerHTML = name;
    	option.value = obj.id;
    	multiselect.appendChild(option);
  	}
  }
});
};

function getHandler()
{
	console.log("GET Animation/id");
	var multiselect = document.getElementById('animationlist');

    if (multiselect.selectedIndex == -1)
    {
    	console.log('not selected');
    	return;
    }
        
    var nameField = document.getElementById("name");
	var authorField = document.getElementById("author");
	var descriptionField = document.getElementById("description");
	var dateCreatedField = document.getElementById("dateCreated");

    var targetID = multiselect.options[multiselect.selectedIndex].value;

    $.ajax({
	  url: fullAddress + 'Animation/' + targetID,
	  type: 'GET',
	  headers:
	  {
	  	'dataType': 'json',
	  	'Access-Control-Allow-Origin': "*"
	  },	  
	  success:  function( data ) 
	  {
	  	data = data.replace(/[\n]/g, '\\n')
				    .replace(/[\r]/g, '\\r')
				    .replace(/[\t]/g, '\\t');

	  	var obj = JSON.parse(data);
	 	nameField.value = obj.name;
	 	authorField.value = obj.author;
	 	descriptionField.value = obj.description;
	 	dateCreatedField.value = obj.dateCreated;
 	 	var space = document.getElementById('animation_space');
		space.value = obj.animations;
	  }

});
};

function updateHandler()
{
	console.log("PUT Animation/id");

	var multiselect = document.getElementById('animationlist');
	if (multiselect.selectedIndex == -1)
    {
    	console.log('not selected');
    	return;
    }
    var targetID = multiselect.options[multiselect.selectedIndex].value;

	var nameField = document.getElementById("name");
	var authorField = document.getElementById("author");
	var descriptionField = document.getElementById("description");
	var dateCreatedField = document.getElementById("dateCreated");
	name = nameField.value
	
	var obj = {};
	obj.author = authorField.value;
	obj.description = descriptionField.value;
	obj.name = name;
	obj.id = targetID;
	obj.dateCreated = dateCreatedField.value;

	var animations;
	if(playing())
	{
		animations = fullAnimation;
	}
	else
	{
		var textArea = document.getElementById("animation_space");
		animations =  textArea.value;
	}
	obj.animations = animations;

	$.ajax({
	  url: fullAddress + 'Animation',
	  data: JSON.stringify(obj),
	  type: 'PUT',
	  headers: 
	  {
	  	'dataType': 'json',
	  	'Access-Control-Allow-Origin': "*"
	  },	  
	  success:  function( data ) 
	  {
	  	alert("Updated " + name);
	  	browseHandler();
	  }

	});
	
};

function uploadHandler()
{
	console.log("POST Animation");

	var nameField = document.getElementById("name");
	var authorField = document.getElementById("author");
	var descriptionField = document.getElementById("description");
	var dateCreatedField = document.getElementById("dateCreated");
	
	var currentDate = getCurrentDate();

	var name = nameField.value;

	var obj = {};
	obj.author = authorField.value;
	obj.description = descriptionField.value;
	obj.name = name;
	obj.dateCreated = currentDate;

	var animations;
	if(playing())
	{
		animations = fullAnimation;
	}
	else
	{
		var textArea = document.getElementById("animation_space");
		animations =  textArea.value;
	}
	obj.animations = animations;

	$.ajax({
	  url: fullAddress + 'Animation',
	  data: JSON.stringify(obj),
	  type: 'POST',
	  headers: 
	  {
	  	'dataType': 'json',
	  	'Access-Control-Allow-Origin': "*"
	  },	  
	  success:  function( data ) 
	  {
	  	alert("Uploaded " + name);
	  	browseHandler();
	  }

	});
	dateCreatedField.value = currentDate;
};

function getCurrentDate()
{
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();

	if(dd<10) {
	    dd='0'+dd
	} 

	if(mm<10) {
	    mm='0'+mm
	} 

	today = mm + '/' + dd + '/' +yyyy;
	return today;
};

function deleteHandler()
{
	console.log("DELETE Animation/id");
	
	var multiselect = document.getElementById('animationlist');
	if (multiselect.selectedIndex == -1)
    {
    	console.log('not selected');
    	return;
    }
    var targetID = multiselect.options[multiselect.selectedIndex].value;
    var name = multiselect.options[multiselect.selectedIndex].innerHTML;

    $.ajax({
	  url: fullAddress + 'Animation/' + targetID,
	  type: 'DELETE',
	  headers: 
	  {
	  	'dataType': 'json',
	  	'Access-Control-Allow-Origin': "*"
	  },	  
	  success:  function( data ) 
	  {
	  	alert("Deleted " + name);
	  	browseHandler();
	  }

	});
};

function playing()
{
	return document.getElementById("start").disabled;
};

//assign onclick functions to start and stop buttons
function buttonSetup()
{
	var stopButton = document.getElementById("stop");
	stopButton.disabled = true;
	stopButton.onclick = stopButtonEngaged;
	var startButton = document.getElementById("start");
	startButton.onclick = startButtonEngaged;	
};

//assign onclick functions to drop down "select" lists
function dropDownSetup()
{
	var sizeChoiceMenu = document.getElementsByName("size_choice")[0];
	sizeChoiceMenu.onchange = setFontSize;
};

//assign onclick functions to radio buttons
function radioButtonSetup()
{
	var radioButtons = document.getElementsByName("speed");
	for(var i=0; i < radioButtons.length; i++)
	{
		radioButtons[i].onclick = changeSpeed;
	}
	
}

//set current state of textarea
function setTextAreaLoop()
{
	var textArea = document.getElementById("animation_space");
	textArea.value = animationArray[currentFrame];
	currentFrame = (currentFrame + 1) % totalFrames;
};

//start button has been pressed
function startButtonEngaged()
{
	var startButton = document.getElementById("start");
	startButton.disabled = true;
	var stopButton = document.getElementById("stop");
	stopButton.disabled = false;
	var textArea = document.getElementById("animation_space");
	var stringInTextArea =  textArea.value;
	fullAnimation = stringInTextArea;
	animationArray = stringInTextArea.split("=====\n");
	totalFrames = animationArray.length;
	currentFrame = 0;
	animationInterval = window.setInterval(function() {setTextAreaLoop()}, currentSpeed);
};

//stop button has been pressed
function stopButtonEngaged()
{
	var stopButton = document.getElementById("stop");
	stopButton.disabled = true;
	var startButton = document.getElementById("start");
	startButton.disabled = false;
	window.clearInterval(animationInterval);
	var textArea = document.getElementById("animation_space");
	textArea.value = fullAnimation;
};

//font size choice has swtiched
function setFontSize()
{
	var textArea = document.getElementById("animation_space");
	textArea.style.fontSize = this.value + "pt";
};


//loop speed choice has swtiched
function changeSpeed()
{
	currentSpeed = SPEEDS[this.value];
	var stopButton = document.getElementById("stop");
	if(stopButton.disabled == false)
	{
		window.clearInterval(animationInterval);
		animationInterval = window.setInterval(function() {setTextAreaLoop()}, currentSpeed);
	}
};

