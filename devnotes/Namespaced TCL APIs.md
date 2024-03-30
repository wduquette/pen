[[Home]] #idea 

## Background

I am using TCL extensions in several contexts:

-  As a file format for defining Calendars, Histories, and Drawings.
- As commands used in a general scripting environment
- A History should be able to load a Calendar from a file; but it should also be able to define a calendar in place.

## Ensembles

I usually use ensembles for this purpose.

- Histories are defined using the `history` ensemble
- Calendars are defined using the `calendar` ensemble

But making it an ensemble makes the command prefix that much longer.

## Another Way

Suppose I defined APIs using namespaces instead of ensembles.

- `history::entity`, `history::event`, etc.

When using `DataFiles.loadHistory`, I import those procs into the global namespace.  A history file can write `entity`, `event`, etc.

- This would make it reasonable for these commands to be ensembles!

Then, I define `history <script>` or `history eval <script>` that uses `namespace eval` under the covers to allow code like this:

```tcl
set name "JoeP"
set myHistory [history {
    entity joe $name person
    event AD-2024-03-30 "$name says hello" joe
    ...
}]
```
