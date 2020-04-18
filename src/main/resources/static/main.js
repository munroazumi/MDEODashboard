var columnDefs = [
    {headerName: 'ID', field: 'id', sortable: true, resizable: true, flex: 1},
    {headerName: 'Status', field: 'status', sortable: true, resizable: true, flex : 1},
    {headerName: 'Name', field: 'name', sortable: true, resizable: true, flex: 2},
    {headerName: 'Time Started', field: 'timeStarted', sortable: true, resizable: true, flex: 2},
    {headerName: 'Time Finished', field: 'timeFinished', sortable: true, resizable: true, flex: 2},
];

var gridOptions = {
    columnDefs: columnDefs,
};

var eGridDiv = document.querySelector('#myGrid');

new agGrid.Grid(eGridDiv, gridOptions);

agGrid.simpleHttpRequest({
    url: '/getjobs'
})
    .then(function(data) {
        gridOptions.api.setRowData(data);
    })

function refreshGrid() {
    console.log('calling refresh')
    agGrid.simpleHttpRequest({
        url: '/getjobs'
    })
        .then(function(data) {
            gridOptions.api.setRowData(data);
        })
}