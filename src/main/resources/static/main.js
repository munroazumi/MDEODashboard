var columnDefs = [
    {headerName: 'ID', field: 'id', sortable: true, resizable: true, flex: 1},
    {headerName: 'Status', field: 'status', sortable: true, resizable: true, flex : 1},
    {headerName: 'Name', field: 'name', sortable: true, resizable: true, flex: 2},
    {headerName: 'Time Elapsed', field: 'timeElapsed', sortable: true, resizable: true, flex: 2},
    {headerName: 'Time Finished', field: 'timeFinished', sortable: true, resizable: true, flex: 2},
];

var gridOptions = {
    columnDefs: columnDefs,
};

var eGridDiv = document.querySelector('#myGrid');

new agGrid.Grid(eGridDiv, gridOptions);

agGrid.simpleHttpRequest({
    url: '/addjson'
})
    .then(function(data) {
        gridOptions.api.setRowData(data);
    })

/*
var con = mysql.createConnection({
    host: "localhost",
    user: "mdeo",
    password: "asdf",
    database: "MDEOProject"
});

con.connect(function(err) {
    if (err) throw err;
    con.query("SELECT * FROM job", function (err, result, fields) {
        if (err) throw err;
        console.log(result);
    });
});
*/

function refreshGrid() {
    console.log('calling refresh')
    agGrid.simpleHttpRequest({
        url: '/getjson'
    })
        .then(function(data) {
            gridOptions.api.setRowData(data);
        })
}