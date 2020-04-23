var columnDefs2 = [
    {headerName: 'ID', field: 'id', sortable: true, resizable: true},
    {headerName: 'Status', field: 'status', sortable: true, resizable: true},
    {headerName: 'Name', field: 'name', sortable: true, resizable: true},
    {headerName: 'Time Elapsed', field: 'timeElapsed', sortable: true, resizable: true},
    {headerName: 'Time Finished', field: 'timeFinished', sortable: true, resizable: true},
];

var rowData2 = [
    {id: '0', status: 'running', name: 'makeoneup', timeElapsed: '4 minutes', timeFinished: 'N/A'},
    {id: '1', status: 'running', name: 'abc', timeElapsed: '8 minutes', timeFinished: 'N/A'},
    {id: '2', status: 'finished', name: 'jessicajones', timeElapsed: '29 minutes', timeFinished: '9/4/2020 08:34:48'},
    {id: '3', status: 'finished', name: 'thisgud', timeElapsed: '40 minutes', timeFinished: '4/4/2020 13:43:09'},
    {id: '4', status: 'finished', name: 'yesir', timeElapsed: '43 minutes', timeFinished: '4/4/2020 16:20:20'},
]

var gridOptions2 = {
    columnDefs: columnDefs2,
    //rowData: rowData2
};

var eGridDiv2 = document.querySelector('#resultsGrid');

agGrid2 = new agGrid.Grid(eGridDiv2, gridOptions2);

agGrid2.simpleHttpRequest({
    url: 'Fakedata.json'
})
    .then(function(data2) {
        gridOptions2.api.setRowData(data2);
    })
