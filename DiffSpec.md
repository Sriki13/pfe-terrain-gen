# JSON Diff file format

Setting the parameter `jsonDiff` to true in the map context will allow the server to only send what has changed compared to the previous sent JSON, drastically reducing the size of the HTTP payloads.

If no json has been generated before using the route, the complete map JSON is sent.

## Mesh changes

The whole map JSON is sent again.

## Property changes

### On faces, vertices and edges

This concerns changes that influence the properties of the elements of the mesh, but not its structure: `face_props, vertex_props, edge_props`.

There are 3 scenarios to support:

- **The properties of an element have not changed.** In that case, the diff Json will not contain the element properties.

- **The properties of an element have changed.** In this case, all the properties of this element will be sent again: the existing properties from the earlier map should be discarded and replaced with the new ones.

- **An element does not have properties anymore.** In this case, an item in the property array will be placed with key of the element, but the value of the property `"vals"` will be replaced by the marker `"o":"r"` indicating that the properties of the element must be deleted.

Below is a small example where the properties of the face #1 have changed, while the properties of the face #2 have been deleted.
```
{
	"face_props": [
	    {
		    "vals": [{ "p": "waterKind", "v": "OCEAN" }],
		    "key": 1 
		},
	    {
		    "o": "r",   // Marker for removal
		    "key": 2 
	    },
	 ]
 }
```

### On the map itself

This concerns changes on the properties stored at the root of the island model, such as `trees`.
The 3 same scenarios must be supported.

- **A property has not changed** In that case, the diff Json will not contain that property.

- **If a property has changed**, it will be sent again and must be replaced in the previous map, the same way it works with faces, vertices and edges properties.

- **If one or more properties have been deleted**, a Json array in the root of the island will indicate which properties are concerned. 

Below is a small example where the property `trees` has changed, while the property `creeks` and `emergency` have been deleted.
```
{
	"trees": [
		{
			"x": 109.2612520466977,
	        "y": 290.3381990379468,
	        "z": 49.81304852328566
	    }
	 ],
	 "rm": ["creeks", "emergency"]
 }
```
