import java.awt.*;
import java.lang.*;
import java.util.*;
import java.awt.geom.Line2D;

public class MyPointSet extends Vector<MyPoint> {

    private int imin, imax, xmin, xmax;
    private boolean xySorted;
    private Vector<MyPoint>  theHull;
    public static final long serialVersionUID = 24362462L;
    public MyPointSet() {
	xySorted = false;
    }

    public void addPoint(int x, int y) {
	MyPoint p = new MyPoint(x,y);
	addElement(p);
	xySorted = false;
    }

    private int next(int i) {
	return (i = (i+1) % size());
    }

    private int previous(int i) {
	return (i = (i-1+size()) % size());
    }

    private int hullnext(int i) {
	return (i = (i+1) % theHull.size());
    }

    private int hullprevious(int i) {
	return (i = (i-1+theHull.size()) % theHull.size());
    }

    public void sortByXY() {
	int i;
	MyPoint p, q;
	boolean clean;

	// Task 1
    while (true) {
		boolean swapped = false; 
		for (i=1;i<size();i++) {
			p = elementAt(i-1);
			q = elementAt(i); 
			if(p.x > q.x) {
				setElementAt(p,i);
				setElementAt(q,i-1);
				swapped = true;
			} else if (p.x == q.x && p.y > q.y) {
				setElementAt(p,i);
				setElementAt(q,i-1);
				swapped = true; 
			}
		}
		if (!swapped) {
				break;
		}
	}

	xySorted = true;

	//Here's some code that is useful for debugging 
    for (i=0; i<size(); i++) {
	    p = elementAt(i);
	    System.out.println(i+": "+p.x+" "+p.y);
	}
	
	return;
    }

    private void enumerateHull() {  
	
	System.out.println("");
	System.out.print("Current chain is: ");
	for (int index=0; index<theHull.size(); index++) {
		MyPoint tmppoint;
	    tmppoint = theHull.elementAt(index);
	    System.out.print(" ("+tmppoint.x+", "+tmppoint.y+")");
	}
	System.out.println("");
    }

    private int removeChain(int bottom, int top) {
	// removes the chain between bottom+1 and top-1 inclusive
	// N.B. the size of the hull decreases by 1 at each step
	// returns the index of the last valid element

	int i, howmany;
	MyPoint q;

	System.out.println("  Removing chain between "+bottom+" and "+top+
			   " in hull of size "+theHull.size());

	if (bottom == top) return bottom; // nothing to remove

	if (bottom < top) {
	    howmany = top-bottom-1;
	    System.out.println("   0 I want "+howmany+" elements");
	    for (i=0; i<howmany; i++) {
		q = theHull.elementAt(bottom+1);
		System.out.println("   0 Removing element at "+bottom+1+": ("+q.x+", "+q.y+")");
		theHull.removeElementAt(bottom+1);
	    }
	}

	else { // top < bottom so wrap along chain end
	    System.out.println(" \n  WRAPPING AROUND THE END \n");
	    howmany = theHull.size()-bottom-1;
	    System.out.println("   1 I want "+howmany+" elements between "+(bottom+1)+" and "+(theHull.size()-1)+" inclusive");
	    for (i=0; i<howmany; i++) {
		q = theHull.elementAt(bottom+1);
		System.out.println("   1 Removing element at "+(bottom+1)+": ("+q.x+", "+q.y+")");
		theHull.removeElementAt(bottom+1);
	    }
	    howmany = top;
	    System.out.println("   plus "+howmany+" elements between "+0+" and "+(top-1)+" inclusive");
	    for (i=0; i<howmany; i++) {
		// could remove top-1 but then need to change top
		q = theHull.elementAt(0);
		System.out.println("   2 Removing element at "+0+": ("+q.x+", "+q.y+")");
		theHull.removeElementAt(0);
	    }

	    if (bottom >= theHull.size()) bottom = theHull.size()-1;
	}

	return bottom; // index of last valid element
    }
    
    private void hullIncremental() {    
	int k,i,n, howmany;
	MyPoint p, q, r;
	MyPoint topelem, nextelem, botelem, prevelem;
	int top, bottom;
	
	n = size();

	if (n<3) {
	    System.out.println("\u0007Can't compute convex hull");
	    return;
	}

	theHull   = new Vector<MyPoint>(n,n);

	if (!xySorted) sortByXY();

	// provisionally add the first three non-identical non-collinear points
    q = elementAt(0);
	p = elementAt(1); 

	int iterStartIdx = 2;
	r = elementAt(iterStartIdx); 

	while(r.isEqual(q) || r.isEqual(p)) {
		iterStartIdx++; 
		r = elementAt(iterStartIdx);
	}
	while(p.inBetween(q,r)) {
		p = r; 
		iterStartIdx++; 
		r = elementAt(iterStartIdx);
	}

	theHull.insertElementAt(q,0);
	if(p.left(q,r)) {
		theHull.insertElementAt(p,0);
	} else {
		theHull.insertElementAt(p,1); 
	}
	theHull.insertElementAt(r,2);

	//Task 4
	// OPTIONAL CODE HERE TDOO

	// initialise the ends of the chain-to-be-removed 
	// as the rightmost point in the hull
    // That is, top=bottom= "index of rightmost point in hull"
	top=bottom=2;

    //Task 3
	// YOUR CODE GOES HERE

    //Loop through the remaining points
	for(i=iterStartIdx;i<size();i++) {
		// next light-source is p = CH[k]
		p=elementAt(i);

		// Catch for equivalent point
		if(p.isEqual(theHull.elementAt(top)) || p.isEqual(theHull.elementAt(bottom))) {
			continue;
		}

		// find lit chain
		while(!p.left(theHull.elementAt(top), theHull.elementAt(hullnext(top))) || 
			p.collinear(theHull.elementAt(top), theHull.elementAt(hullnext(top)))) {
			top = hullnext(top); 
		}
		while(!p.left(theHull.elementAt(hullprevious(bottom)), theHull.elementAt(bottom)) || 
			p.collinear(theHull.elementAt(hullprevious(bottom)), theHull.elementAt(bottom))) {
			bottom = hullprevious(bottom);
		}


		// remove lit chain
		bottom = removeChain(bottom, top);
		//Add the next point

		theHull.insertElementAt(p,(bottom+1));
		enumerateHull();

		// construct next chain starting at last inserted element
		if (top != bottom) top=bottom=bottom+1; 
	//End loop
	}
    }

    public Polygon hullDraw() {
	int i;
	MyPoint q;
	Polygon p;

	p = new Polygon();
	System.out.println("The Hull has size "+theHull.size());
	System.out.println("The Hull is:");
	for (i=0; i<theHull.size(); i++) {
	    q = theHull.elementAt(i);
	    System.out.println("-> ("+q.x+", "+q.y+")");
	    p.addPoint(q.x,q.y);
	}	
	System.out.println("===================================");
	return p;
    }

    public Polygon hullThePolygon() {
	int i;
	Polygon chp;

	sortByXY();

	hullIncremental();
	chp = hullDraw();

	return chp;
    }
}
