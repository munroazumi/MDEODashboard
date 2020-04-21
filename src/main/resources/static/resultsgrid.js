var columnDefs2 = [
    {headerName: 'ID', field: 'id'},
    {headerName: 'Status', field: 'status'},
    {headerName: 'Name', field: 'name'},
    {headerName: 'Time Elapsed', field: 'timeElapsed'},
    {headerName: 'Time Finished', field: 'timeFinished'},
];

var gridOptions2 = {
    columnDefs: columnDefs2,
    defaultColDef: {
        sortable: true,
        resizable: true,
        flex: 1,
    }
};

var eGridDiv2 = document.querySelector('#resultsGrid');

new agGrid.Grid(eGridDiv2, gridOptions2);

agGrid.simpleHttpRequest({
    url: 'Fakedata.json'
})
    .then(function(data2) {
        gridOptions2.api.setRowData(data2);
    })
