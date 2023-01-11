import React from 'react';
import { cleanup, render, screen } from '@testing-library/react';
import App, { initdist } from './App';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom'
import GraphVis, {dots, fullyloggedin, textbuff, genrandomstring, mag, sech, radsort, linsort, getdata, getdatastrings, getdatamatches, repulse, towardsort, sortshift, updatecamcenter, getsortname, getparamname} from './GraphVis';

const genericGraphviz = GraphVis(
    "uid2", //CurrentGoogleUser
    true, //spotifyLinked
    true, //usersloaded
    false, //fetchingusers
    new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]), //usersongparams
    new Map([[0, ["songName", "songArtist"]], [1, ["songName2", "songArtist2"]]]), //userdatastrings, 
    new Map([[1, [[0], [0]]], [0, [[1], [1]]]]), //matchesdata,
    .5, //Timer
    ["uid1", "uid2"], //userIDs
    [[0, 1, 2], [1, 2, 3]], //CircleData [us]
    1, //SortParameter for default
    1, //SortIndex for sort style
    [1, 0, 0], //camcenter, 
    1, //SelectIndex, 
    0, //zoomval, 
    false, //zoomed, 
    false, //alltime, 
    0, //curruserindex,
    (bool: boolean) => {}, //Setalltime, 
    (num: number) => {}, //setSelectIndex, 
    (bool: boolean) => {}, //setZoomed, 
    (num: number) => {}, //setSortParameter, 
    (num: number) => {}, //setSortIndex
    );

beforeEach(() => {
});

afterEach(() => {
    cleanup;
});

//testing based on the idea of 'tests for this front-end application should be written in terms of what the end-user can perceive'

test('renders app header', () => {
    render(<App />);
    const headerElement = screen.getByLabelText("App header")
    expect(headerElement).toBeInTheDocument();
});

test('renders sign in box', () => {
    render(<App />);
    const loginElement = screen.getByLabelText("Click here to sign in with google")
    expect(loginElement).toBeInTheDocument();
});

test('renders logo', () => {
    render(<App />);
    const logoElement = screen.getByLabelText("Logo for the tunedin website")
    expect(logoElement).toBeInTheDocument();
});

test('renders spotify button', () => {
    render(<App />);
    const loginElement = screen.getByLabelText("Click here to log in to spotify")
    expect(loginElement).toBeInTheDocument();
});

test('render loading screen in graphviz if fetching users', () => {
    render(GraphVis(
        "uid2", //CurrentGoogleUser
        true, //spotifyLinked
        false, //usersloaded
        true, //fetchingusers
        new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]), //usersongparams
        new Map([[0, ["songName", "songArtist"]], [1, ["songName2", "songArtist2"]]]), //userdatastrings, 
        new Map([[1, [[0], [0]]], [0, [[1], [1]]]]), //matchesdata,
        .5, //Timer
        ["uid1", "uid2"], //userIDs
        [[0, 1, 2], [1, 2, 3]], //CircleData [us]
        1, //SortParameter for default
        1, //SortIndex for sort style
        [1, 0, 0], //camcenter, 
        1, //SelectIndex, 
        0, //zoomval, 
        false, //zoomed, 
        false, //alltime, 
        0, //curruserindex,
        (bool: boolean) => {}, //Setalltime, 
        (num: number) => {}, //setSelectIndex, 
        (bool: boolean) => {}, //setZoomed, 
        (num: number) => {}, //setSortParameter, 
        (num: number) => {}, //setSortIndex
        ));
        const loadingElement = screen.getByLabelText("loading screen")
        expect(loadingElement).toBeInTheDocument();

        let errorMsg;
        try {
            const notElement = screen.getByLabelText("user sidebar for displaying your current matches")
        } catch {
            errorMsg = "error thrown"
        }
        expect(errorMsg).toBe("error thrown");
});

test('render nno sidebar if spotify is not linked', () => {
    render(GraphVis(
        "uid2", //CurrentGoogleUser
        false, //spotifyLinked
        true, //usersloaded
        false, //fetchingusers
        new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]), //usersongparams
        new Map([[0, ["songName", "songArtist"]], [1, ["songName2", "songArtist2"]]]), //userdatastrings, 
        new Map([[1, [[0], [0]]], [0, [[1], [1]]]]), //matchesdata,
        .5, //Timer
        ["uid1", "uid2"], //userIDs
        [[0, 1, 2], [1, 2, 3]], //CircleData [us]
        1, //SortParameter for default
        1, //SortIndex for sort style
        [1, 0, 0], //camcenter, 
        1, //SelectIndex, 
        0, //zoomval, 
        false, //zoomed, 
        false, //alltime, 
        0, //curruserindex,
        (bool: boolean) => {}, //Setalltime, 
        (num: number) => {}, //setSelectIndex, 
        (bool: boolean) => {}, //setZoomed, 
        (num: number) => {}, //setSortParameter, 
        (num: number) => {}, //setSortIndex
        ));
        const sidebar = screen.getByLabelText("hidden")
        expect(sidebar).toBeInTheDocument();
});

test('render nno sidebar if spotify is not linked', () => {
    render(genericGraphviz)
    const sidebar = screen.getByLabelText("sidebar")
    expect(sidebar).toBeInTheDocument();
    const matches = screen.getByLabelText("click here to view the top 5 users you match with of all time below")
    expect(matches).toBeInTheDocument();
});

// TESTS FOR FUNCTIONS IN GRAPHVIZ
test('textbuff()', () => {
    const result = textbuff("hiiii", 2)
    expect(result).toBe("hi...")
});

test('dots()', () => {
    const result = dots(1)
    expect(result).toBe("...")
});

test('fullyloggedin()', () => {
    const result = fullyloggedin(true, "className")
    expect(result).toBe("className")
    const result1 = fullyloggedin(false, "className")
    expect(result1).toBe("hidden")
});

test('genradnomstring()', () => {
    const result = genrandomstring(4)
    expect(result.length).toBe(4)
});

test('genrandomstring() empty edge case', () => {
    const result = genrandomstring(0)
    expect(result).toBe('')
});

test('initdist() ', () => {
    const result: Array<Array<number>> = initdist(50)
    expect(result.length).toBe(50) 
    expect(result[2].length).toBe(3) // 50 items, each a 3d array
});


test('mag()', () => {
    const result = mag([1, 2, 3, 4, 5])
    expect((result < 3.61 && result > 3.59)).toBe(true)
    const result2 = mag([])
    expect(result2).toBe(NaN) // DYLAN DYLAN note that edge case outputs NaN
});

test('radsort()', () => {
        render(genericGraphviz);
        const point1 = [0, 1, 2] // id, x, y
        const result1 = radsort(point1, 0, true, 0, new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]))
        expect([result1[0], Math.floor(result1[1]), Math.floor(result1[2])]).toStrictEqual([0, 357, 715])
        const point = [0, 0, 0] // id, x, y
        const result = radsort(point, 0, true, 0, new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]))
        expect([result[0], Math.floor(result[1]), Math.floor(result[2])]).toStrictEqual([0, 0, 0])
});

test('linsort()', () => {
    genericGraphviz;

    const point1 = [0, 1, 2] // id, x, y
    const result1 = linsort(point1, 1, true, 0, new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]))
    expect([result1[0], Math.floor(result1[1]), Math.floor(result1[2])]).toStrictEqual([0, 1, -200])

    const point = [0, 0, 0] // id, x, y
    const result = linsort(point, 1, true, 0, new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]))
    expect([result[0], Math.floor(result[1]), Math.floor(result[2])]).toStrictEqual([0, 1, -200])
});

test('repulse()', () => {
    render(genericGraphviz);
    const result: Array<number> = repulse([0, 2, 3], [1, 2, 6])
    expect(result.length).toBe(2)
    expect(result[0]).toBe(0)
    expect(Math.floor(result[1])).toBe(-1)

    const result1: Array<number> = repulse([0, 2, 3], [1, 2, 3])
    expect(result1.length).toBe(2)
    expect(result1[0]).toBe(0)
    expect(Math.floor(result1[1])).toBe(0)
});

test('towardsort()', () => {
    render(genericGraphviz)
    const point1 = [0, 1, 2] // id, x, y
    const result1 = towardsort(point1, 0, [[0, 1, 2], [1, 2, 3]], radsort, 1, true, 0, new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]))
    expect([result1[0], Math.floor(result1[1]), Math.floor(result1[2])]).toStrictEqual([0, -1, 1])

});

test('getdata()', () => {
    render(genericGraphviz)
    const result = getdata(1, 0, new Map([[0, [.1, .2, .3, .4, .5, .6]], [1, [.1, .2, .3, .4, .5, .6]]]))
    expect(result).toBe(.1)
    const result2 = getdata(0, 5, new Map([[0, [.1, .2, .3, .4, .5, .6]], [1, [.1, .2, .3, .4, .5, .6]]]))
    expect(result2).toBe(.6)
});

test('getdatamatches()', () => {
    render(genericGraphviz)
    const result = getdatamatches(0, true, 0, new Map([[1, [[0], [0]]], [0, [[1], [1]]]]))
    expect(result).toBe(1)
    const result2 = getdatamatches(1, false, 0, new Map([[1, [[0], [0]]], [0, [[1], [1]]]]))
    expect(result2).toBe(0)
});

test('getdatastrings()', () => {
    render(genericGraphviz)
    const result = getdatastrings(0, 0, new Map([[0, ["songName", "songArtist"]], [1, ["songName2", "songArtist2"]]]))
    expect(result).toBe("songName")
    const result2 = getdatastrings(1, 1, new Map([[0, ["songName", "songArtist"]], [1, ["songName2", "songArtist2"]]]))
    expect(result2).toBe("songArtist2")
});

test('sortShift()', () => {
    render(genericGraphviz)

    const result = sortshift([[0, 1, 2], [1, 2, 3]], 2, linsort, .5, true, 0, new Map([[0, [.1, .2, .3, .4, .5, .6]], [1, [.1, .2, .3, .4, .5, .6]]]))
    expect([result[0][0], Math.floor(result[0][1]), Math.floor(result[0][2])]).toStrictEqual([0, -1, 0])
    expect([result[1][0], Math.floor(result[1][1]), Math.floor(result[1][2])]).toStrictEqual([1, 3, 4])

    const result1 = sortshift([[0, 1, 2], [1, 2, 3]], 0, radsort, .5, true, 0, new Map([[0, [1, 1, 1, 1, 1, 1]], [1, [1, 1, 1, 1, 1, 1]]]))
    expect([result1[0][0], Math.floor(result1[0][1]), Math.floor(result1[0][2])]).toStrictEqual([0, -1, 0])
    expect([result1[1][0], Math.floor(result1[1][1]), Math.floor(result1[1][2])]).toStrictEqual([1, 3, 4])

});

test('updatecamcenter()', () => {
    const result1 = updatecamcenter([0, 0, 0], [1, 1, 1])
    expect([Math.floor(result1[0]*100), Math.floor(result1[1]*100), Math.floor(result1[2]*100)]).toStrictEqual([3, 3, 3])
});
