import React, { useState, Dispatch, SetStateAction, useEffect, useCallback } from 'react';
import GraphVis, { camtarg, getsortmethod, slidenum, sortshift, updatecamcenter } from './GraphVis'
import logo from './logo.svg';
import './styles/App.css';
import {firebaseConfig, privClientID, privClientSecret} from './private/firebaseconfig'
import * as d3 from 'd3';
import { signInWithGoogle } from './GoogleLogin';
import { SpotifyLoginButton} from './SpotifyAuth';
import { getFirestore, doc, setDoc, getDoc } from "firebase/firestore"; 
import { initializeApp } from "firebase/app";
import { updateuserdata } from './backendhandler';

// DONE TODOS:
//[DONE]: We need to be able to render a whole ton of circles.
//[DONE]: Add the actual parameters we want
//[DONE]: Change background color
//[DONE]: Add camera controls
//[DONE]: Make button fade in dynamically
//[DONE]: Make the sidebar change when you zoom in
//[DONE]: Add number displays in the sidebar
//[DONE]: Make a pretty logo and slap it in
//[DONE]: Add the current user
//[DONE]: Add matches and matches navigation
//[DONE]: Add variables for everything we're actually going to use
//[DONE]: Account for when the google user is not logged in but the spotify is already linked
//[DONE]: Make google thing vanish after you've logged in
//[DONE]: Make the website actually look the way we want it to
//[DONE]: FIX THE INFINITE LOOP PLEASE
//[DONE]: make a button to change the sorting parameter!
//[DONE]: Get frontend to communicate with backend
//[DONE]: Set curruser when you log in!
//[DONE]: Make current user index update when you log in! Edit the initdist functions
//[DONE]: Aria label the heck out of everything
//[DONE]: Re-render users once you've logged in fully!
//[DONE]: Fix long title formatting!
//[DONE]: Add a pretty gradient bar on the side to denote how things are being sorted from bottom to top
//[DONE]: Turn off the user outline circles when you aren't logged in
//[DONE]: If you can't find the current google user in the userlist(mocked data), consider making a separate screen?
//[DONE]: add a cute credits/about us section

// FRONTEND TODO:
// TODO: COMMENT EVERYTHING
// TODO: CLEAN UP AND TEST

// set this to be true or false depending on if you want to display mocked users or not. This value should match
// the value of the USING_MOCKS environment variable on the backend.
let usingmocks: boolean = false;

// A function takes maxnum and randomly generates that many user coordinates across our screen.
// The general template for a set of "user coordinates" is as follows:
// [<user index number>, <user x position>, <user y position>]
// The x and y position are used and updated to make the user bubbles move across the screen,
// while the user index is used to identify each bubble with an actual user's data
export function initdist(num: number): Array<Array<number>>{
  let returnarr: Array<Array<number>> = new Array<Array<number>>();
  for(let i = 0; i < num; i++){
      // Add a randomly generated user coordinate to the stack
      returnarr.push([i,1500*(Math.random()-0.5),600*(Math.random()-0.5)])
  }
  return returnarr;
}

// This returns a promise that will only resolve when all users' maps have been updated
async function setuserdata(userIDs:Array<string>, usersongparams: Map<number, Array<number>>,
  userdatastrings: Map<number, Array<string>>, matchesdata: Map<number, Array<Array<number>>>, 
  setusersongparams: ((map: Map<number, Array<number>>) => void), setuserdatastrings: ((map: Map<number, Array<string>>) => void), 
  setmatchesdata: ((map: Map<number, Array<Array<number>>>) => void)): Promise<void[]>{
  // console.log(googleuserid)
  const range: Array<number> = Array.from(Array(userIDs.length).keys())
  const promises: Array<Promise<void>> = range.map((i:number)=>{
      return updateuserdata(i, userIDs,usersongparams,userdatastrings,matchesdata, setusersongparams, setuserdatastrings, setmatchesdata)})
  return Promise.all(promises)
}

// This takes in a google ID as a string and locates it within our list of loaded google IDs,
// then returns the index at which it lies. If it does not lie within the list, it returns a default value of 0.
function getcurruserindex(userIDs: Array<string>,googleuser: string, spotifylinked: boolean): number{
  if(userIDs.includes(googleuser)){
      return userIDs.indexOf(googleuser)
  }
  else{
    if(googleuser != "" && spotifylinked){
      userunregistered = true;
    }
    console.log(userIDs + " does not contain " + googleuser)
    return 0;
  }
}

// This function returns a small button that, when clicked, displays a window with information about Tunedin
function aboutus(){
  return <div className="aboutustext" aria-label = "click for more info about tunedin">
    <svg width = "75" height = "50">
    <rect 
      key = "paramsortbutton"
      width = "75"
      height = "30"
      x= "0"
      y= "0"
      rx="5"
      ry="5"
      onClick={()=>{aboutusdisplay = true}}
      >
      </rect>
      <text className = "whitetext" x = "9" y = "22"
      onClick={()=>{aboutusdisplay = true}}>
      About
      </text>
    </svg>
  </div>
}

// Aria label moved up here since it's so long
const infotext = "Tunedin is a musical social media app designed by Samantha Minars, Denise Tamesis, Chance Emerson, and Dylan Lee. Find your perfect music match with Tunedin's sophisticated algorithm! Expand your music universe and keep up with your friends' latest jams."

// This function checks if "aboutusdisplay" is true or not, and returns a window with information about Tunedin if so.
function aboutusinfo(){
  if(aboutusdisplay){
  return  <div className = "aboutusinfo" aria-label = {infotext}>
    <svg width = "510" height = "400">
        <rect 
          key = "textbox"
          width = "500"
          height = "380"
          x= "5"
          y= "5"
          rx="5"
          ry="5"
          />
          <text className = "whitetext2" x = "20" y = "25"
          onClick={()=>{aboutusdisplay = false}}>
          {"[Click right here to close this window]"}
          </text>

          <text className = "whitetext2" x = "20" y = "75">
          Tunedin is a musical social media app designed by Samantha Minars, 
          </text>

          <text className = "whitetext2" x = "20" y = "100">
          Denise Tamesis, Chance Emerson, and Dylan Lee.
          </text>

          <text className = "whitetext2" x = "20" y = "125">
          Find your perfect music match with Tunedin's sophisticated algorithm!
          </text>
          
          <text className = "whitetext2" x = "20" y = "150">
          Expand your music universe and keep up with your friends' latest jams.
          </text>
          
          <text className = "whitetext2" x = "20" y = "200">
          TUNE IN. YOU WIN.
          </text>

          <text className = "whitetext2" x = "20" y = "225">
          It's a win-win, take TUNEDIN for a spin.
          </text>

          <text className = "whitetext2" x = "20" y = "250">
          Tune out the din, tune in with TUNEDIN!
          </text>

          <text className = "whitetext2" x = "20" y = "275">
          You'll never not grin when tuning TUNEDIN in.
          </text>

          <text className = "whitetext2" x = "20" y = "300">
          FALL for TUNEDIN like a bowling pin!
          </text>

          <text className = "whitetext2" x = "20" y = "325">
          Your life wears thin. Repair your skin with TUNEDIN.
          </text>

          <text className = "whitetext2" x = "20" y = "350">
          Use TUNEDIN or forfeit your shins. Win and sin in the din in TUNEDIN.
          </text>

          <text className = "whitetext2" x = "20" y = "375">
          help me
          </text>
    </svg>
  </div>
  }
}

// A basic angular wave that we use for making the warning sign bounce around the screen
function sawtooth(x: number): number{
  return Math.abs(((2*x) % 2)-1)
}

// A simple function for showing the spotify button when we're logged in to google
function hidebutton(CurrentGoogleUser: string): string{
  if(CurrentGoogleUser == ""){
    return "hidden"
  }
  else{
    return "spotifybutton"
  }
}

// A simple function that takes in a boolean "userunregistered" and returns a string if it is false.
// Used when determining if a user has logged in fully but is not one of
// Tunedin's spotify-developer-greenlisted users.
function showgoogleuserstring(CurrentGoogleUser: string){
  if(userunregistered && !usingmocks ){
    return ""
  }
  else{
    return CurrentGoogleUser
  }
}

// If a user is not one of Tunedin's spotify developer greenlisted users, this function returns a set of
// warnings that bounce around the screen, instructing the user to contact the team at team.tunein@gmail.com 
// to be allowed access into the app
function warningscreen(Timer: number){
  if(userunregistered && !usingmocks){
    return <svg className="warningscreen" width = "100%" height = "100%">
      {[0,1,2,3,4].map((x)=> {return <image href="https://i.ibb.co/8dYvrr8/Screen-Shot-2022-12-20-at-12-40-41-AM.png"
      x={0+800*sawtooth(3*(Timer+x/5))}
      y={0 + 275*sawtooth(2*(Timer+x/5))}
      />})}
      <image href="https://i.ibb.co/8dYvrr8/Screen-Shot-2022-12-20-at-12-40-41-AM.png"
      x={800*0.5}
      y={275*0.5}
      />
    </svg>
  }
}

// Function for checking if spotify has been linked with our app
async function checkSpotifyLinked() {
  const app = initializeApp(firebaseConfig);
  const db = getFirestore(app);
  const localUID = localStorage.getItem("UID")
  if (localUID != null) {
    let userDoc = await getDoc(doc(db, "users", localUID))
    let data = userDoc.data()
    console.log(data)
    // if the user rexists
    if (data != undefined) {
      // if the user spotify is linked
      if (data["refreshToken"] != "" && data["refreshToken"] != undefined) {
        console.log("there is a refresh token")
        return true;
      // if the user spotify is not linked
      } else {
        console.log("there was no refresh token detected")
        return false;
      }
    // if the user does not exist
    } else {
      console.log("this user has not logged into their google account yet")
      return false;
    }
  }
  else{
    console.log("local UID NULL")
  }
}

let userunregistered = false;

let aboutusdisplay = false;

function App() {
  // String storing the current google user.
  const [CurrentGoogleUser, SetCurrentGoogleUser] = useState<string>("");
  // Boolean denoting if spotify is linked or not.
  const [SpotifyLinked, setspotifyLinked] = useState<boolean>(false);
  // Boolean denoting if the app is currently fetching the user's spotify data or not.
  const [fetchingusers, setfetchingusers] = useState<boolean>(false)
  // Boolean denoting if the app has finished fetching and loading the user data from the backend. Used for loading screen.
  const [usersloaded, setusersloaded] = useState<boolean>(false);
  // A map from a user's number index to an array of their current song's parameters, aka Acousticness, Energy, etc.
  const [usersongparams, setusersongparams] = useState<Map<number, Array<number>>>(new Map)
  // A map from a user's number index to an array of their string parameters, aka user display name, song title, song artist.
  const [userdatastrings, setuserdatastrings] = useState<Map<number, Array<string>>>(new Map)
  // A map from a user's number index to two arrays of indexes of users who they match with. First array is current matches, second array is top matches of all time.
  const [matchesdata, setmatchesdata] = useState<Map<number,Array<Array<number>>>> (new Map)
  // A global timer variable that loops from 0 to 1. Used for onscreen animations.
  const [Timer, setTimer] = useState<number>(0)
  // An array of all current user IDs, stored as strings
  const [userIDs, setuserIDs] = useState<Array<string>>([])
  // An array of arrays of all current user bubble positions onscreen. Each smaller array is of the form [user index, x position, y position].
  const [CircleData, setCircleData] = useState<number[][]>([]);
  // A number denoting the parameter that we sort the user bubbles by.
  const [SortParameter, setSortParameter] = useState<number>(0);
  // A number denoting the way that we sort the user bubbles on screen, aka linearly or radially.
  const [SortIndex, setSortIndex] = useState<number>(1);
  // An array representing the current camera state of the form [zoom factor, x position, y position]
  const [camcenter, Setcamcenter] = useState<number[]>([1,0,0]);
  // A number denoting the current user selected. Set to be 0 by default.
  const [SelectIndex, setSelectIndex] = useState<number>(0);
  // A number denoting how zoomed in we are on screen via the camera position. Used for UI element transitions.
  const [zoomval, Setzoomval] = useState<number>(0);
  // A boolean denoting whether or not we are zoomed in to a user, aka if that user has been selected or not.
  const [zoomed, Setzoomed] = useState<boolean>(false);
  // A boolean denoting whether or not we should display all time user matches vs current user matches.
  const [alltime, Setalltime] = useState<boolean>(false);
  // A number denoting the index of the current user of the webpage in all other arrays and maps.
  const [curruserindex, Setcurruserindex] = useState<number>(0);

  // A number denoting the speed at which circles move on screen.
  const Speed = 10;

  useEffect(() => {
    const interval = setInterval(() => {
      // Increase the Timer variable regardless of what's going on, since we have animations in all cases
      setTimer((Timer + 0.001) % 1)

      // Initial login logic. Updating Google and Spotify login booleans.
      if(CurrentGoogleUser != ""){
        // There is a non-empty google user
        if(!SpotifyLinked){
          // SpotifyLinked boolean is still false
          checkSpotifyLinked().then((result)=>{
            if(result == true){
              // Spotify has been linked, update SpotifyLinked boolean.
            setspotifyLinked(true);
            }
          });
        }
      }

      // UPDATING ARRAY OF USER IDS
      if(!usersloaded){
        // Users have not been loaded yet
          if (!fetchingusers){
              // We have not already sent out a fetch request for users, so we start a new one.
              setfetchingusers(true)
              fetch("http://localhost:3232/get-all-user-ids").then((respjson)=>{
                  respjson.json().then((respobj)=>{
                    // Take the list of user IDs from our backend API and set our userIDs array to be equal to the response
                      const ids = respobj.ids
                      setuserIDs(ids)
                      // Set the position of our user bubbles to be a random distribution of points onscreen
                      setCircleData(initdist(ids.length))
                      // Load the song features of all users
                      fetch("http://localhost:3232/load-song-features").then(()=>{
                        // Load the matches/connections between users
                          fetch("http://localhost:3232/load-connections").then(()=>{
                            // Update all maps with newly loaded user data
                              setuserdata(ids, usersongparams, userdatastrings, matchesdata, setusersongparams, setuserdatastrings, setmatchesdata).then(()=>
                              {
                                // Once this promise evaluates, all users have been loaded. Set usersloaded to be true.
                                  setusersloaded(true)
                              })
                          })
                      })
                  })
              })
          }
      }
      if(usersloaded){
      // If users have been completely loaded, update these values every 10 milliseconds.
      Setcurruserindex(getcurruserindex(userIDs,CurrentGoogleUser, SpotifyLinked))
      setCircleData(sortshift(CircleData, SortParameter, getsortmethod(SortIndex), Speed, SpotifyLinked, curruserindex, usersongparams))
      Setcamcenter(updatecamcenter(camcenter,
      camtarg([4,CircleData[SelectIndex][1],CircleData[SelectIndex][2]],
            [1,0,0],zoomed)))
        Setzoomval(1-((camcenter[0]-2)/2))
      document.documentElement.style.setProperty('--sidebar-mode', zoomval.toString());
      document.documentElement.style.setProperty('--timeslidermode', slidenum(alltime).toString());
      }
    }
    , 10);
     return () => clearInterval(interval);
})

  return (
    <div className="App">
      <p className="App-header" aria-label = "App header">
        {/* Google login button and tunedin logo */}
        {(CurrentGoogleUser == "") && 
        <button className="google-button" onClick = {()=>{let x = signInWithGoogle(SetCurrentGoogleUser)}} aria-label = "Click here to sign in with google">Sign in With Google</button>}
        <img className = "tuneinlogo" src="https://i.ibb.co/rFTJDTr/tuneinlogo2.png" aria-label = "Logo for the tunedin website"/>
      </p>
      {/* Main user bubble visualizer. Takes in a whole bunch of parameters for testing */}
      {GraphVis(showgoogleuserstring(CurrentGoogleUser), SpotifyLinked && (!userunregistered || usingmocks), usersloaded, fetchingusers, usersongparams, userdatastrings, matchesdata, Timer,
       userIDs, CircleData, SortParameter, SortIndex, camcenter, SelectIndex, zoomval, zoomed, alltime, curruserindex,
       Setalltime, setSelectIndex, Setzoomed, setSortParameter, setSortIndex)}
       {/* Spotify login button */}
      <div className = {hidebutton(CurrentGoogleUser)} aria-label = "Click here to log in to spotify">
        <SpotifyLoginButton clientId={"213450855ac44f5aa842c2359939fded"} 
        redirectUri={'http://localhost:3000/callback/'} 
        clientSecret = {'9771ae6d19724806b33c585b57068127'} 
        setUser2 = {SetCurrentGoogleUser} 
        spotifyLinked = {SpotifyLinked}
        setspotifyLinked = {setspotifyLinked}
        setusersloaded = {setusersloaded}
        setfetchingusers = {setfetchingusers}
        />
      </div>
      {/* About us button and window popup */}
      {aboutus()}      
      {aboutusinfo()}
      {/* Warning screen if user is unregistered */}
      {warningscreen(Timer)}
    </div>
  );
}

export default App;
