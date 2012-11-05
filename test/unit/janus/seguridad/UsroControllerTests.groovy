package janus.seguridad



import org.junit.*
import grails.test.mixin.*

@TestFor(UsroController)
@Mock(Usro)
class UsroControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/usro/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.usroInstanceList.size() == 0
        assert model.usroInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.usroInstance != null
    }

    void testSave() {
        controller.save()

        assert model.usroInstance != null
        assert view == '/usro/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/usro/show/1'
        assert controller.flash.message != null
        assert Usro.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/usro/list'


        populateValidParams(params)
        def usro = new Usro(params)

        assert usro.save() != null

        params.id = usro.id

        def model = controller.show()

        assert model.usroInstance == usro
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/usro/list'


        populateValidParams(params)
        def usro = new Usro(params)

        assert usro.save() != null

        params.id = usro.id

        def model = controller.edit()

        assert model.usroInstance == usro
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/usro/list'

        response.reset()


        populateValidParams(params)
        def usro = new Usro(params)

        assert usro.save() != null

        // test invalid parameters in update
        params.id = usro.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/usro/edit"
        assert model.usroInstance != null

        usro.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/usro/show/$usro.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        usro.clearErrors()

        populateValidParams(params)
        params.id = usro.id
        params.version = -1
        controller.update()

        assert view == "/usro/edit"
        assert model.usroInstance != null
        assert model.usroInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/usro/list'

        response.reset()

        populateValidParams(params)
        def usro = new Usro(params)

        assert usro.save() != null
        assert Usro.count() == 1

        params.id = usro.id

        controller.delete()

        assert Usro.count() == 0
        assert Usro.get(usro.id) == null
        assert response.redirectedUrl == '/usro/list'
    }
}
